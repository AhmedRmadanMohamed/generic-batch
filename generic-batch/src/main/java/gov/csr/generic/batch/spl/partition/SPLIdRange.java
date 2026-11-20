package gov.csr.generic.batch.spl.partition;

/**
 * Inclusive ID range discovered from the prepared SPL work set.
 */
public record SPLIdRange(long minId, long maxId) {

    public SPLIdRange {
        if (minId > maxId) {
            throw new IllegalArgumentException("minId must be less than or equal to maxId");
        }
    }
}
