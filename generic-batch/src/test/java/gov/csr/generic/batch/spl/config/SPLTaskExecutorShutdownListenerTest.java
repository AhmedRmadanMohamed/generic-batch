package gov.csr.generic.batch.spl.config;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SPLTaskExecutorShutdownListenerTest {

    @Test
    void shutsDownWorkerPoolAfterJobCompletion() {
        ThreadPoolTaskExecutor executor = new SPLTaskExecutorFactory().create("splAddressJob", 2);
        JobExecution jobExecution = mock(JobExecution.class);
        JobInstance jobInstance = mock(JobInstance.class);
        when(jobExecution.getJobInstance()).thenReturn(jobInstance);
        when(jobInstance.getJobName()).thenReturn("splAddressJob");

        new SPLTaskExecutorShutdownListener(executor).afterJob(jobExecution);

        assertThat(executor.isRunning()).isFalse();
    }
}
