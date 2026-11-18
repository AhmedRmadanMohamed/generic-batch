package gov.csr.generic.batch.listener;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.repository.BatchJobConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Transactional
public class BatchJobRunningListener implements JobExecutionListener {

    private final BatchJobConfigRepository batchJobConfigRepository;

    @Override
    @Transactional
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        BatchJobConfig config = batchJobConfigRepository.findByJobName(jobName).orElseThrow();
        if (config.isRunning()) {
            throw new IllegalStateException("Job " + jobName + " already running");
        }
        config.setRunning(true);
        config.setLastStartTime(LocalDateTime.now());
        batchJobConfigRepository.save(config);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        batchJobConfigRepository.findByJobName(jobName)
                .ifPresent(config -> {
                    config.setRunning(false);
                    config.setLastFinishTime(LocalDateTime.now());
                    batchJobConfigRepository.save(config);
                });
    }
}
