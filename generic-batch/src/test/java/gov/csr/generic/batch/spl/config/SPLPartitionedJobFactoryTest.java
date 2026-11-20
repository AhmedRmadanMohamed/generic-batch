package gov.csr.generic.batch.spl.config;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.listener.BatchJobRunningListener;
import gov.csr.generic.batch.listener.RestartFlagChunkListener;
import gov.csr.generic.batch.listener.RestartFlagStepExecutionListener;
import gov.csr.generic.batch.spl.model.SPLAddressResult;
import gov.csr.generic.batch.spl.model.SPLPersonInput;
import gov.csr.generic.batch.spl.procedure.SPLStoredProcedureExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.PlatformTransactionManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class SPLPartitionedJobFactoryTest {

    private final SPLPartitionedJobFactory factory = new SPLPartitionedJobFactory(
            mock(JobRepository.class),
            mock(PlatformTransactionManager.class),
            mock(BatchJobRunningListener.class),
            mock(RestartFlagChunkListener.class),
            mock(RestartFlagStepExecutionListener.class),
            new SPLTaskExecutorFactory(),
            mock(SPLStoredProcedureExecutor.class));

    @Test
    void buildsProcedureThenMasterWorkerJobUsingDatabaseJobNameAndThreadCount() {
        BatchJobConfig config = config("splAddressJob", 3, 25);

        Job job = factory.buildJob(
                config,
                mock(Partitioner.class),
                mock(ItemReader.class),
                mock(ItemProcessor.class),
                mock(ItemWriter.class));

        assertThat(job.getName()).isEqualTo("splAddressJob");
        assertThat(job.getStepNames())
                .contains("splAddressJob-procedure", "splAddressJob-master");
    }

    @Test
    void distinctConfiguredJobNamesProduceDistinctSpringBatchJobs() {
        Job splJob = factory.buildJob(
                config("splAddressJob", 2, 20),
                mock(Partitioner.class),
                mock(ItemReader.class),
                mock(ItemProcessor.class),
                mock(ItemWriter.class));

        Job nicJob = factory.buildJob(
                config("personBasicInfo", 2, 20),
                mock(Partitioner.class),
                mock(ItemReader.class),
                mock(ItemProcessor.class),
                mock(ItemWriter.class));

        assertThat(splJob.getName()).isNotEqualTo(nicJob.getName());
        assertThat(splJob.getStepNames()).doesNotContain(nicJob.getName() + "-master");
        assertThat(nicJob.getStepNames()).doesNotContain(splJob.getName() + "-master");
    }

    @Test
    void rejectsMissingThreadCount() {
        BatchJobConfig config = config("splAddressJob", null, 20);

        assertThatThrownBy(() -> factory.buildJob(
                config,
                mock(Partitioner.class),
                mock(ItemReader.class),
                mock(ItemProcessor.class),
                mock(ItemWriter.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("THREAD_COUNT must be greater than zero for job [splAddressJob]");
    }

    @Test
    void rejectsMissingChunkSize() {
        BatchJobConfig config = config("splAddressJob", 2, null);

        assertThatThrownBy(() -> factory.buildJob(
                config,
                mock(Partitioner.class),
                mock(ItemReader.class),
                mock(ItemProcessor.class),
                mock(ItemWriter.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CHUNK_SIZE must be greater than zero for job [splAddressJob]");
    }

    private BatchJobConfig config(String jobName, Integer threadCount, Integer chunkSize) {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName(jobName);
        config.setThreadCount(threadCount);
        config.setChunkSize(chunkSize);
        return config;
    }
}
