package gov.socialdev.nic.model.responses;

import gov.socialdev.nic.model.common.Header;
import gov.socialdev.nic.model.common.PersonRelationDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PersonRelationDetailsResponse {
    private Header header;
    private PersonRelationDetails result;
}
