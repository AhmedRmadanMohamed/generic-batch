package gov.socialdev.spl.model.response;

import gov.socialdev.spl.model.address.AddressesDTO;
import gov.socialdev.spl.model.common.Header;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaudiPostResponse {
    private Header header;
    private AddressesDTO data;
}
