package gov.csr.generic.batch.listener;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.entity.BatchStatus;
import gov.csr.generic.batch.repository.BatchJobConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestartFlagChunkListener implements ChunkListener {

    private final BatchJobConfigRepository repository;

    @Override
    public void afterChunk(ChunkContext context) {

        String jobName =
                context.getStepContext()
                        .getStepExecution()
                        .getJobExecution()
                        .getJobInstance()
                        .getJobName();

        BatchJobConfig config = repository.findByJobName(jobName).orElse(null);
        if (config == null) {
            return;
        }

        boolean needStop =
                config.isNeedRestart()
                || config.getBatchStatus() == BatchStatus.DISABLED;

        if (needStop) {
            log.info("Stopping job [{}] after current chunk (needRestart={}, status={})",
                    jobName, config.isNeedRestart(), config.getBatchStatus());
            context.getStepContext()
                    .getStepExecution()
                    .setTerminateOnly();
        }
    }
}
