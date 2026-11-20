package gov.csr.generic.batch.spl.procedure;

import gov.csr.generic.batch.entity.BatchJobConfig;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Resolves the procedure name exclusively from BATCH_JOB_CONFIG.PROCEDURE_NAME.
 *
 * Only an unquoted Oracle identifier is accepted here. This prevents SQL
 * fragments from being supplied through configuration. Qualified/package
 * procedure naming must be added only after its exact convention is confirmed.
 */
@Component
public class SPLProcedureNameResolver {

    private static final Pattern ORACLE_PROCEDURE_IDENTIFIER =
            Pattern.compile("[A-Za-z][A-Za-z0-9_$#]{0,127}");

    public String resolve(BatchJobConfig config) {
        Objects.requireNonNull(config, "BatchJobConfig must not be null");

        String configuredName = config.getProcedureName();
        if (configuredName == null || configuredName.isBlank()) {
            throw new IllegalStateException(
                    "PROCEDURE_NAME is not configured for job [" + config.getJobName() + "]");
        }

        String procedureName = configuredName.trim();
        if (!ORACLE_PROCEDURE_IDENTIFIER.matcher(procedureName).matches()) {
            throw new IllegalStateException(
                    "Invalid Oracle procedure identifier in PROCEDURE_NAME for job ["
                            + config.getJobName() + "]");
        }

        return procedureName;
    }
}
