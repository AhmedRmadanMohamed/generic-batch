package gov.csr.generic.batch.spl.procedure;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.service.JobConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * Executes the SPL preparation procedure using the name stored in
 * BATCH_JOB_CONFIG.PROCEDURE_NAME.
 *
 * The job configuration is loaded from the database for each execution.
 * Procedure arguments are deliberately supplied by the caller because their
 * names, types, direction, and signature must come from the confirmed DB
 * procedure contract.
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
     * exact procedure configured in its PROCEDURE_NAME column.
     *
     * @param jobName      JOB_NAME used only to load the corresponding config row
     * @param inParameters input parameters keyed by their exact database parameter names
     * @return OUT parameters and any values exposed by Spring JDBC
     */
    public Map<String, Object> executeForJob(
            String jobName,
            Map<String, ?> inParameters) {

        if (jobName == null || jobName.isBlank()) {
            throw new IllegalArgumentException("jobName must not be blank");
        }

        BatchJobConfig config = jobConfigService.getJobConfig(jobName);
        return execute(config, inParameters);
    }

    /**
     * Executes the procedure named in the supplied persisted configuration.
     * This overload is useful when the caller has already loaded the config
     * row from the database.
     */
    public Map<String, Object> execute(
            BatchJobConfig config,
            Map<String, ?> inParameters) {

        String procedureName = procedureNameResolver.resolve(config);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        if (inParameters != null) {
            inParameters.forEach(parameters::addValue);
        }

        log.info("Executing configured SPL procedure [{}] for job [{}]",
                procedureName, config.getJobName());

        return simpleJdbcCallFactory.create(jdbcTemplate)
                .withProcedureName(procedureName)
                .execute(parameters);
    }

    @FunctionalInterface
    interface SimpleJdbcCallFactory {
        SimpleJdbcCall create(JdbcTemplate jdbcTemplate);
    }
}
