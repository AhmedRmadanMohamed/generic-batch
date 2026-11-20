package gov.csr.generic.batch.spl.procedure;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SPLProcedureTaskletTest {

    @Test
    void executesConfiguredProcedureForItsJobWithoutParameters() throws Exception {
        SPLStoredProcedureExecutor executor = mock(SPLStoredProcedureExecutor.class);
        SPLProcedureTasklet tasklet = new SPLProcedureTasklet(executor, "splAddressJob");

        RepeatStatus status = tasklet.execute(
                mock(StepContribution.class),
                mock(ChunkContext.class));

        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(executor).executeForJob("splAddressJob");
    }

    @Test
    void rejectsBlankJobName() {
        assertThatThrownBy(() -> new SPLProcedureTasklet(
                mock(SPLStoredProcedureExecutor.class), " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("jobName must not be blank");
    }
}
