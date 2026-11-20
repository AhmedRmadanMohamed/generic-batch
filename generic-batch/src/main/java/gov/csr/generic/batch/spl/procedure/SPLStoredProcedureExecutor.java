package gov.csr.generic.batch.spl.procedure;

import gov.csr.generic.batch.entity.BatchJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * Executes the SPL preparation procedure using the name configured in
 * BATCH_JOB_CONFIG.PROCEDURE_NAME.
 *
 * Procedure arguments are supplied by the caller because their names, types,
 * direction, and signature are defined by the database procedure contract.
 * Spring JDBC uses database metadata to resolve the procedure parameters.
 */
@Slf4j
@Service
public class SPLStoredProcedureExecutor {

    private final JdbcTemplate jdbcTemplate;
    private final SPLProcedureNameResolver procedureNameResolver;

    public SPLStoredProcedureExecutor(
            JdbcTemplate jdbcTemplate,
            SPLProcedureNameResolver procedureNameResolver) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
        this.procedureNameResolver = Objects.requireNonNull(
                procedureNameResolver, "procedureNameResolver must not be null");
    }

    /**
     * Executes the configured procedure.
     *
     * @param config       persisted batch job configuration containing PROCEDURE_NAME
     * @param inParameters input parameters keyed by the exact database parameter names;
     *                     an empty map is valid for a procedure with no input parameters
     * @return OUT parameters and any values exposed by Spring JDBC
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

        return new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName)
                .execute(parameters);
    }
}
