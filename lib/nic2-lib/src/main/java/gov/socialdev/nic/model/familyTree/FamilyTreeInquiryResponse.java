package gov.socialdev.nic.model.familyTree;

import gov.socialdev.nic.model.BaseIntegrationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyTreeInquiryResponse extends BaseIntegrationResponse {
    private FamilyTreeInquiryResult result;
}
