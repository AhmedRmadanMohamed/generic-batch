package gov.csr.generic.batch.spl.model;

/**
 * Minimal input required by the SPL lookup: the source row identifier and social ID.
 */
public record SPLPersonInput(long id, String socialId) {

    public SPLPersonInput {
        if (socialId == null || socialId.isBlank()) {
            throw new IllegalArgumentException("socialId must not be blank");
        }
    }
}
