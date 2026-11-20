package gov.csr.generic.batch.spl.model;

/**
 * Outcome of processing one source person through the SPL integration.
 *
 * These are application-level outcomes, not database status codes.
 * They must be mapped to the confirmed SPL target schema by the Writer.
 */
public enum SPLProcessingStatus {
    SUCCESS,
    EMPTY_RESULT,
    NO_RECORD_FOUND,
    SERVICE_UNAVAILABLE,
    INVALID_RESPONSE,
    FAILED
}
