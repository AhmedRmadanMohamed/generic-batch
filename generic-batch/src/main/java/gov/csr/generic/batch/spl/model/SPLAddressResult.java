package gov.csr.generic.batch.spl.model;

import gov.socialdev.spl.model.address.AddressDTO;

import java.util.List;
import java.util.Objects;

/**
 * Processing outcome for one source person, preserving all addresses returned by SPL.
 *
 * A person may have zero, one, or multiple addresses. The status distinguishes
 * an empty business result from technical failures.
 */
public record SPLAddressResult(
        long sourceId,
        String socialId,
        List<AddressDTO> addresses,
        SPLProcessingStatus status,
        String errorMessage) {

    public SPLAddressResult {
        if (socialId == null || socialId.isBlank()) {
            throw new IllegalArgumentException("socialId must not be blank");
        }
        addresses = addresses == null ? List.of() : List.copyOf(addresses);
        status = Objects.requireNonNull(status, "status must not be null");
    }

    public static SPLAddressResult success(
            long sourceId, String socialId, List<AddressDTO> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return emptyResult(sourceId, socialId);
        }
        return new SPLAddressResult(
                sourceId, socialId, addresses, SPLProcessingStatus.SUCCESS, null);
    }

    public static SPLAddressResult emptyResult(long sourceId, String socialId) {
        return new SPLAddressResult(
                sourceId, socialId, List.of(), SPLProcessingStatus.EMPTY_RESULT, null);
    }

    public static SPLAddressResult noRecordFound(
            long sourceId, String socialId, String errorMessage) {
        return new SPLAddressResult(
                sourceId, socialId, List.of(), SPLProcessingStatus.NO_RECORD_FOUND, errorMessage);
    }

    public static SPLAddressResult failure(
            long sourceId,
            String socialId,
            SPLProcessingStatus status,
            String errorMessage) {
        if (status != SPLProcessingStatus.SERVICE_UNAVAILABLE
                && status != SPLProcessingStatus.INVALID_RESPONSE
                && status != SPLProcessingStatus.FAILED) {
            throw new IllegalArgumentException("Failure status is not a technical failure: " + status);
        }
        return new SPLAddressResult(sourceId, socialId, List.of(), status, errorMessage);
    }
}
