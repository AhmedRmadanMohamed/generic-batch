package gov.csr.generic.batch.spl.procedure;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Objects;

/**
 * Runs the parameterless procedure configured for this SPL job before the
 * master step partitions the prepared work set.
 */
public final class SPLProcedureTasklet implements Tasklet {

    private final SPLStoredProcedureExecutor procedureExecutor;
    private final String jobName;

    public SPLProcedureTasklet(SPLStoredProcedureExecutor procedureExecutor, String jobName) {
        this.procedureExecutor = Objects.requireNonNull(
                procedureExecutor, "procedureExecutor must not be null");
        if (jobName == null || jobName.isBlank()) {
            throw new IllegalArgumentException("jobName must not be blank");
        }
        this.jobName = jobName;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        procedureExecutor.executeForJob(jobName);
        return RepeatStatus.FINISHED;
    }
}
