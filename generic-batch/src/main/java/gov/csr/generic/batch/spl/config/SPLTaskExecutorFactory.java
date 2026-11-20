package gov.csr.generic.batch.spl.config;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Creates a bounded worker pool for one SPL partitioned job execution.
 *
 * The pool size is sourced from BATCH_JOB_CONFIG.THREAD_COUNT. A zero-capacity
 * queue prevents the pool from accumulating an unbounded backlog; the master
 * partition step submits at most gridSize (= threadCount) worker tasks.
 */
@Component
public class SPLTaskExecutorFactory {

    public ThreadPoolTaskExecutor create(String jobName, int threadCount) {
        if (jobName == null || jobName.isBlank()) {
            throw new IllegalArgumentException("jobName must not be blank");
        }
        if (threadCount < 1) {
            throw new IllegalArgumentException("threadCount must be greater than zero");
        }

        String safeJobName = jobName.replaceAll("[^A-Za-z0-9_-]", "-");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCount);
        executor.setMaxPoolSize(threadCount);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("spl-" + safeJobName + "-worker-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
