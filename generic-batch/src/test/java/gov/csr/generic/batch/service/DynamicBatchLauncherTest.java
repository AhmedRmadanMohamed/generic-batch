package gov.csr.generic.batch.service;

import gov.csr.generic.batch.entity.BatchJobConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicBatchLauncherTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private JobResolver jobResolver;

    @Mock
    private JobExplorer jobExplorer;

    @Test
    void skipsLaunchWhenTheSameJobIsAlreadyRunning() throws Exception {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("personBasicInfo");

        when(jobExplorer.findRunningJobExecutions("personBasicInfo"))
                .thenReturn(Set.of(mock(JobExecution.class)));

        DynamicBatchLauncher launcher =
                new DynamicBatchLauncher(jobLauncher, jobResolver, jobExplorer);

        launcher.runEnabledJobs(config, "personBasicInfo");

        verify(jobResolver, never()).resolveJob(config);
        verify(jobLauncher, never()).run(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }
}
