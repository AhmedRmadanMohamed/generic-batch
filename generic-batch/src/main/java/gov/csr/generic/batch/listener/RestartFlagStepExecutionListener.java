package gov.csr.generic.batch.listener;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.entity.BatchStatus;
import gov.csr.generic.batch.repository.BatchJobConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestartFlagStepExecutionListener implements StepExecutionListener {

    private final BatchJobConfigRepository repository;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        String jobName =
                stepExecution.getJobExecution()
                        .getJobInstance()
                        .getJobName();

        BatchJobConfig config = repository.findByJobName(jobName).orElse(null);
        if (config == null) {
            return stepExecution.getExitStatus();
        }

        boolean needStop =
                config.isNeedRestart()
                || config.getBatchStatus() == BatchStatus.DISABLED;

        if (needStop) {
            stepExecution.setTerminateOnly();
            return ExitStatus.STOPPED;
        }

        return stepExecution.getExitStatus();
    }
}
