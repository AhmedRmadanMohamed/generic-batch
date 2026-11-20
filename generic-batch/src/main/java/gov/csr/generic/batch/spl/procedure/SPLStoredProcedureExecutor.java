package gov.csr.generic.batch.spl.procedure;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.service.JobConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * Executes the parameterless SPL preparation procedure using the name stored
 * in BATCH_JOB_CONFIG.PROCEDURE_NAME.
 */
@Slf4j
@Service
public class SPLStoredProcedureExecutor {

    private final JdbcTemplate jdbcTemplate;
    private final SPLProcedureNameResolver procedureNameResolver;
    private final JobConfigService jobConfigService;
    private final SimpleJdbcCallFactory simpleJdbcCallFactory;

    public SPLStoredProcedureExecutor(
            JdbcTemplate jdbcTemplate,
            SPLProcedureNameResolver procedureNameResolver,
            JobConfigService jobConfigService) {
        this(jdbcTemplate, procedureNameResolver, jobConfigService, SimpleJdbcCall::new);
    }

    SPLStoredProcedureExecutor(
            JdbcTemplate jdbcTemplate,
            SPLProcedureNameResolver procedureNameResolver,
            JobConfigService jobConfigService,
            SimpleJdbcCallFactory simpleJdbcCallFactory) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
        this.procedureNameResolver = Objects.requireNonNull(
                procedureNameResolver, "procedureNameResolver must not be null");
        this.jobConfigService = Objects.requireNonNull(
                jobConfigService, "jobConfigService must not be null");
        this.simpleJdbcCallFactory = Objects.requireNonNull(
                simpleJdbcCallFactory, "simpleJdbcCallFactory must not be null");
    }

    /**
     * Loads the job configuration from BATCH_JOB_CONFIG and executes the
     * parameterless procedure configured in its PROCEDURE_NAME column.
     *
     * @param jobName JOB_NAME used only to load the corresponding config row
     * @return OUT values returned by Spring JDBC; empty for a procedure with no OUT parameters
     */
    public Map<String, Object> executeForJob(String jobName) {
        if (jobName == null || jobName.isBlank()) {
            throw new IllegalArgumentException("jobName must not be blank");
        }

        BatchJobConfig config = jobConfigService.getJobConfig(jobName);
        return execute(config);
    }

    /**
     * Executes the parameterless procedure named in the supplied persisted
     * configuration.
     */
    public Map<String, Object> execute(BatchJobConfig config) {
        String procedureName = procedureNameResolver.resolve(config);

        log.info("Executing parameterless SPL procedure [{}] for job [{}]",
                procedureName, config.getJobName());

        return simpleJdbcCallFactory.create(jdbcTemplate)
                .withProcedureName(procedureName)
                .execute();
    }

    @FunctionalInterface
    interface SimpleJdbcCallFactory {
        SimpleJdbcCall create(JdbcTemplate jdbcTemplate);
    }
}
