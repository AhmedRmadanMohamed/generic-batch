package gov.csr.generic.batch.config;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.listener.BatchJobRunningListener;
import gov.csr.generic.batch.listener.RestartFlagChunkListener;
import gov.csr.generic.batch.listener.RestartFlagStepExecutionListener;
import gov.csr.generic.batch.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenericJobFactory {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ApplicationContext context;
    private final BatchJobRunningListener batchJobRunningListener;
    private final RestartFlagChunkListener restartFlagChunkListener;
    private final RestartFlagStepExecutionListener restartFlagStepExecutionListener;

    public Job buildJob(BatchJobConfig config) {

        String jobName = config.getJobName();
        int threadCount = config.getThreadCount();

        Partitioner highPartitioner = findPartitioner(jobName + "HighPriorityTablePartitioner");
        Partitioner lowPartitioner = findPartitioner(jobName + "LowPriorityTablePartitioner");

        Step highStep = buildPriorityStep(
                jobName + "-HIGH",
                jobName + "HighPriorityReader",
                config.getChunkSize(),
                threadCount,
                jobName,
                highPartitioner);

        Step lowStep = buildPriorityStep(
                jobName + "-LOW",
                jobName + "LowPriorityReader",
                config.getChunkSize(),
                threadCount,
                jobName,
                lowPartitioner);

        return new JobBuilder(jobName, jobRepository)
                .listener(batchJobRunningListener)
                .start(highStep)
                .next(lowStep)
                .build();
    }

    private Partitioner findPartitioner(String beanName) {
        if (context.containsBean(beanName)) {
            return (Partitioner) context.getBean(beanName);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Step buildPriorityStep(
            String stepName,
            String readerBeanName,
            int chunkSize,
            int threadCount,
            String jobName,
            Partitioner partitioner) {

        ItemReader<Object> reader = (ItemReader<Object>) context.getBean(readerBeanName);

        ItemProcessor<Object, Object> processor =
                (ItemProcessor<Object, Object>) context.getBean(jobName + "ItemProcessor");

        ItemWriter<Object> writer =
                (ItemWriter<Object>) context.getBean(jobName + "Writer");

        if (partitioner != null) {
            // ==============================
            // PARTITIONED STEP (master/worker)
            // ==============================
            Step workerStep = new StepBuilder(stepName + "-worker", jobRepository)
                    .chunk(chunkSize, transactionManager)
                    .reader(reader)
                    .processor(processor)
                    .writer(writer)
                    .listener(restartFlagChunkListener)
                    .listener(restartFlagStepExecutionListener)
                    .build();

            return new StepBuilder(stepName, jobRepository)
                    .partitioner(workerStep.getName(), partitioner)
                    .step(workerStep)
                    .gridSize(threadCount)
                    .taskExecutor(Utils.taskExecutor(threadCount))
                    .build();
        }

        // ==============================
        // SIMPLE STEP (no partitioning)
        // ==============================
        return new StepBuilder(stepName, jobRepository)
                .chunk(chunkSize, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(restartFlagChunkListener)
                .listener(restartFlagStepExecutionListener)
                .taskExecutor(Utils.taskExecutor(threadCount))
                .build();
    }

}
