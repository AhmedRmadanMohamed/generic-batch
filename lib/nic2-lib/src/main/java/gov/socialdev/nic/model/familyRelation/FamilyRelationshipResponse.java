package gov.socialdev.nic.model.familyRelation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import gov.socialdev.nic.model.common.Header;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FamilyRelationshipResponse {
    private Header header;
    private FamilyRelationshipData result;
}
