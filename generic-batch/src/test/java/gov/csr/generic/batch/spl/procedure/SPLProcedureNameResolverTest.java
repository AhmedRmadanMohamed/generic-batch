package gov.csr.generic.batch.spl.procedure;

import gov.csr.generic.batch.entity.BatchJobConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SPLProcedureNameResolverTest {

    private final SPLProcedureNameResolver resolver = new SPLProcedureNameResolver();

    @Test
    void resolvesProcedureNameFromProcedureNameColumnNotJobName() {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("splJob");
        config.setProcedureName("PERF_CLONE_ROWS");

        assertThat(resolver.resolve(config)).isEqualTo("PERF_CLONE_ROWS");
    }

    @Test
    void trimsWhitespaceAroundConfiguredProcedureName() {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("splJob");
        config.setProcedureName("  PERF_CLONE_ROWS  ");

        assertThat(resolver.resolve(config)).isEqualTo("PERF_CLONE_ROWS");
    }

    @Test
    void rejectsMissingProcedureName() {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("splJob");

        assertThatThrownBy(() -> resolver.resolve(config))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("PROCEDURE_NAME is not configured for job [splJob]");
    }

    @Test
    void rejectsBlankProcedureName() {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("splJob");
        config.setProcedureName("   ");

        assertThatThrownBy(() -> resolver.resolve(config))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("PROCEDURE_NAME is not configured for job [splJob]");
    }

    @Test
    void rejectsSqlFragmentsInProcedureName() {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("splJob");
        config.setProcedureName("PROC; DROP TABLE USERS");

        assertThatThrownBy(() -> resolver.resolve(config))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Invalid Oracle procedure identifier in PROCEDURE_NAME for job [splJob]");
    }

    @Test
    void acceptsOracleIdentifierCharacters() {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("splJob");
        config.setProcedureName("PERF$CLONE_ROWS#1");

        assertThat(resolver.resolve(config)).isEqualTo("PERF$CLONE_ROWS#1");
    }
}
