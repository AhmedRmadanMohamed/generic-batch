package gov.csr.generic.batch.spl.partition;

import java.util.Optional;

/**
 * Resolves the inclusive ID range of the SPL work set.
 *
 * Implementations must query the confirmed SPL source table and should be
 * invoked only after the parameterless preparation procedure has completed.
 */
@FunctionalInterface
public interface SPLIdRangeProvider {

    Optional<SPLIdRange> findRange();
}
