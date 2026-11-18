package gov.csr.generic.batch.scheduler;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.repository.BatchJobConfigRepository;
import gov.csr.generic.batch.service.DynamicBatchLauncher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchRestartScheduler {

    private final BatchJobConfigRepository repository;
    private final DynamicBatchLauncher launcher;

    @Scheduled(cron = "${generic-batch-restart.scheduler-cron}")
    public void restartStoppedJobs() {

        List<BatchJobConfig> jobs = repository.findByNeedRestartTrueAndRunningFalse();

        jobs.forEach(config -> {
            try {
                log.info("Restart flag detected for job [{}] - preparing to restart", config.getJobName());
                config.setNeedRestart(false);
                repository.save(config);
                log.info("Starting job [{}] after restart",config.getJobName());
                launcher.runEnabledJobs(config, config.getJobName());

            } catch (Exception e) {
                log.error("Failed to restart job {}", config.getJobName(), e);
            }
        });
    }
}