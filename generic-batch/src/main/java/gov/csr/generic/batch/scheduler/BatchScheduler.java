package gov.csr.generic.batch.scheduler;

import gov.csr.generic.batch.config.GenericBatchProperties;
import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.entity.BatchStatus;
import gov.csr.generic.batch.service.DynamicBatchLauncher;
import gov.csr.generic.batch.service.JobConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchScheduler {

    private final DynamicBatchLauncher launcher;
    private final JobConfigService jobConfigService;
    private final GenericBatchProperties properties;

    @Scheduled(cron = "${generic-batch.scheduler-cron}")
    public void trigger() throws Exception {
        String jobName = properties.getJobName();
        BatchJobConfig config = jobConfigService.getJobConfig(jobName);
        if (config == null) {
            log.warn("Can't find job config for jobName: {}", jobName);
            return;
        }
        if (config.getBatchStatus() != BatchStatus.ENABLED) {
            log.info("Job [{}] is disabled", jobName);
            return;
        }

        log.info("Running batch job [{}]", jobName);
        launcher.runEnabledJobs(config, jobName);
    }
}