package gov.csr.generic.batch.spl.model;

import gov.socialdev.spl.model.address.AddressDTO;

import java.util.List;

/**
 * Keeps the source row identity alongside every address returned by SPL.
 * One source person may produce zero, one, or multiple addresses.
 */
public record SPLAddressResult(long sourceId, String socialId, List<AddressDTO> addresses) {

    public SPLAddressResult {
        if (socialId == null || socialId.isBlank()) {
            throw new IllegalArgumentException("socialId must not be blank");
        }
        addresses = addresses == null ? List.of() : List.copyOf(addresses);
    }
}
