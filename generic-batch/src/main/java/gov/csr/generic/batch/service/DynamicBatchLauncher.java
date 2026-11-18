package gov.csr.generic.batch.service;

import gov.csr.generic.batch.config.GenericBatchProperties;
import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.repository.BatchJobConfigRepository;
import gov.csr.generic.batch.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class DynamicBatchLauncher {

    private final JobLauncher jobLauncher;
    private final JobResolver jobResolver;
    private final JobExplorer jobExplorer;

    public DynamicBatchLauncher(JobLauncher jobLauncher, JobResolver jobResolver , JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.jobResolver = jobResolver;
        this.jobExplorer = jobExplorer;

    }

    public void runEnabledJobs(BatchJobConfig config, String jobName) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        log.info("Resolving job from config [{}] at time {}", jobName, Utils.getDateTime(System.currentTimeMillis()));
        boolean running = !jobExplorer.findRunningJobExecutions(config.getJobName()).isEmpty();
        if (running) {
            log.info("Job [{}] already running, skipping", jobName);
            return;
        }
        Job job = jobResolver.resolveJob(config);
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addString("jobName", config.getJobName())
                .addLong("pageSize", config.getPageSize().longValue())
                .addLong("chunkSize", config.getChunkSize().longValue())
                .toJobParameters();
        log.info("Start Job at time [{}] ", Utils.getDateTime(System.currentTimeMillis()));
        jobLauncher.run(job, params);
    }

}
