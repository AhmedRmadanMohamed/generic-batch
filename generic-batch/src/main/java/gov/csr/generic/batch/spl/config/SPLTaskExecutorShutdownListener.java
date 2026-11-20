package gov.csr.generic.batch.spl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;

/**
 * Gracefully shuts down the execution-specific SPL worker pool after the job
 * has completed or failed.
 */
@Slf4j
public class SPLTaskExecutorShutdownListener implements JobExecutionListener {

    private final ThreadPoolTaskExecutor taskExecutor;

    public SPLTaskExecutorShutdownListener(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = Objects.requireNonNull(taskExecutor, "taskExecutor must not be null");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Shutting down SPL worker pool for job [{}]",
                jobExecution.getJobInstance().getJobName());
        taskExecutor.destroy();
    }
}
