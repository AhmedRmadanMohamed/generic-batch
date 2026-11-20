package gov.csr.generic.batch.spl.config;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.listener.BatchJobRunningListener;
import gov.csr.generic.batch.listener.RestartFlagChunkListener;
import gov.csr.generic.batch.listener.RestartFlagStepExecutionListener;
import gov.csr.generic.batch.spl.model.SPLAddressResult;
import gov.csr.generic.batch.spl.model.SPLPersonInput;
import gov.csr.generic.batch.spl.procedure.SPLProcedureTasklet;
import gov.csr.generic.batch.spl.procedure.SPLStoredProcedureExecutor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

/**
 * Builds the SPL job as a sequential preparation step followed by a
 * partitioned master/worker step.
 *
 * The preparation step executes the parameterless procedure configured in
 * BATCH_JOB_CONFIG.PROCEDURE_NAME. The supplied Partitioner should resolve
 * the ID range at partition time (after preparation), e.g. by using
 * SPLDynamicIdRangePartitioner.
 *
 * The supplied reader and writer must be StepScoped (or otherwise safe for
 * one independent instance per worker). Each worker owns its chunk transaction.
 * Source and target SQL are deliberately supplied by the caller because the
 * SPL source/result table schema has not yet been defined in this project.
 */
@Component
public class SPLPartitionedJobFactory {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final BatchJobRunningListener batchJobRunningListener;
    private final RestartFlagChunkListener restartFlagChunkListener;
    private final RestartFlagStepExecutionListener restartFlagStepExecutionListener;
    private final SPLTaskExecutorFactory taskExecutorFactory;
    private final SPLStoredProcedureExecutor procedureExecutor;

    public SPLPartitionedJobFactory(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            BatchJobRunningListener batchJobRunningListener,
            RestartFlagChunkListener restartFlagChunkListener,
            RestartFlagStepExecutionListener restartFlagStepExecutionListener,
            SPLTaskExecutorFactory taskExecutorFactory,
            SPLStoredProcedureExecutor procedureExecutor) {
        this.jobRepository = Objects.requireNonNull(jobRepository, "jobRepository must not be null");
        this.transactionManager = Objects.requireNonNull(
                transactionManager, "transactionManager must not be null");
        this.batchJobRunningListener = Objects.requireNonNull(
                batchJobRunningListener, "batchJobRunningListener must not be null");
        this.restartFlagChunkListener = Objects.requireNonNull(
                restartFlagChunkListener, "restartFlagChunkListener must not be null");
        this.restartFlagStepExecutionListener = Objects.requireNonNull(
                restartFlagStepExecutionListener, "restartFlagStepExecutionListener must not be null");
        this.taskExecutorFactory = Objects.requireNonNull(
                taskExecutorFactory, "taskExecutorFactory must not be null");
        this.procedureExecutor = Objects.requireNonNull(
                procedureExecutor, "procedureExecutor must not be null");
    }

    public Job buildJob(
            BatchJobConfig config,
            Partitioner partitioner,
            ItemReader<SPLPersonInput> reader,
            ItemProcessor<SPLPersonInput, SPLAddressResult> processor,
            ItemWriter<SPLAddressResult> writer) {

        Objects.requireNonNull(config, "BatchJobConfig must not be null");
        Objects.requireNonNull(partitioner, "partitioner must not be null");
        Objects.requireNonNull(reader, "reader must not be null");
        Objects.requireNonNull(processor, "processor must not be null");
        Objects.requireNonNull(writer, "writer must not be null");

        String jobName = config.getJobName();
        if (jobName == null || jobName.isBlank()) {
            throw new IllegalArgumentException("Configured JOB_NAME must not be blank");
        }
        if (config.getThreadCount() == null || config.getThreadCount() < 1) {
            throw new IllegalArgumentException("THREAD_COUNT must be greater than zero for job [" + jobName + "]");
        }
        if (config.getChunkSize() == null || config.getChunkSize() < 1) {
            throw new IllegalArgumentException("CHUNK_SIZE must be greater than zero for job [" + jobName + "]");
        }

        int threadCount = config.getThreadCount();
        int chunkSize = config.getChunkSize();
        String procedureStepName = jobName + "-procedure";
        String workerStepName = jobName + "-worker";
        String masterStepName = jobName + "-master";

        ThreadPoolTaskExecutor taskExecutor = taskExecutorFactory.create(jobName, threadCount);
        try {
            Step procedureStep = new StepBuilder(procedureStepName, jobRepository)
                    .tasklet(new SPLProcedureTasklet(procedureExecutor, jobName), transactionManager)
                    .listener(restartFlagStepExecutionListener)
                    .build();

            Step workerStep = new StepBuilder(workerStepName, jobRepository)
                    .<SPLPersonInput, SPLAddressResult>chunk(chunkSize, transactionManager)
                    .reader(reader)
                    .processor(processor)
                    .writer(writer)
                    .listener(restartFlagChunkListener)
                    .listener(restartFlagStepExecutionListener)
                    .build();

            Step masterStep = new StepBuilder(masterStepName, jobRepository)
                    .partitioner(workerStepName, partitioner)
                    .step(workerStep)
                    .gridSize(threadCount)
                    .taskExecutor(taskExecutor)
                    .build();

            return new JobBuilder(jobName, jobRepository)
                    .listener(batchJobRunningListener)
                    .listener(new SPLTaskExecutorShutdownListener(taskExecutor))
                    .start(procedureStep)
                    .next(masterStep)
                    .build();
        } catch (RuntimeException exception) {
            taskExecutor.destroy();
            throw exception;
        }
    }
}
