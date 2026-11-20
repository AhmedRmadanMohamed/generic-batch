package gov.csr.generic.batch.spl.procedure;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.service.JobConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SPLStoredProcedureExecutorTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final JobConfigService jobConfigService = mock(JobConfigService.class);
    private final SPLProcedureNameResolver procedureNameResolver = new SPLProcedureNameResolver();
    private final SimpleJdbcCall simpleJdbcCall = mock(SimpleJdbcCall.class);

    private final SPLStoredProcedureExecutor executor = new SPLStoredProcedureExecutor(
            jdbcTemplate,
            procedureNameResolver,
            jobConfigService,
            ignored -> simpleJdbcCall);

    @Test
    void loadsProcedureNameFromDatabaseConfigColumnAndExecutesWithoutParameters() {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("splAddressJob");
        config.setProcedureName("SPL_ADDRESS_LOOKUP_FROM_DB");
        Map<String, Object> expectedResult = Map.of();

        when(jobConfigService.getJobConfig("splAddressJob")).thenReturn(config);
        when(simpleJdbcCall.withProcedureName("SPL_ADDRESS_LOOKUP_FROM_DB"))
                .thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.execute()).thenReturn(expectedResult);

        Map<String, Object> result = executor.executeForJob("splAddressJob");

        assertThat(result).isSameAs(expectedResult);
        verify(jobConfigService).getJobConfig("splAddressJob");
        verify(simpleJdbcCall).withProcedureName("SPL_ADDRESS_LOOKUP_FROM_DB");
        verify(simpleJdbcCall).execute();
    }

    @Test
    void rejectsMissingProcedureNameBeforeExecuting() {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("splAddressJob");
        config.setProcedureName(null);
        when(jobConfigService.getJobConfig("splAddressJob")).thenReturn(config);

        assertThatThrownBy(() -> executor.executeForJob("splAddressJob"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("PROCEDURE_NAME is not configured for job [splAddressJob]");

        verify(simpleJdbcCall, never()).withProcedureName(any());
        verify(simpleJdbcCall, never()).execute();
    }

    @Test
    void rejectsBlankJobNameWithoutLoadingConfiguration() {
        assertThatThrownBy(() -> executor.executeForJob("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("jobName must not be blank");

        verify(jobConfigService, never()).getJobConfig(any());
    }

    @Test
    void rejectsSqlFragmentsFromProcedureNameColumn() {
        BatchJobConfig config = new BatchJobConfig();
        config.setJobName("splAddressJob");
        config.setProcedureName("PROC_NAME; DROP TABLE X");
        when(jobConfigService.getJobConfig("splAddressJob")).thenReturn(config);

        assertThatThrownBy(() -> executor.executeForJob("splAddressJob"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Invalid Oracle procedure identifier in PROCEDURE_NAME for job [splAddressJob]");

        verify(simpleJdbcCall, never()).withProcedureName(any());
        verify(simpleJdbcCall, never()).execute();
    }
}
