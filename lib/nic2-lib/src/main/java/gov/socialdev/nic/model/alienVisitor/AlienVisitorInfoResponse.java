package gov.socialdev.nic.model.alienVisitor;

import gov.socialdev.nic.model.BaseIntegrationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlienVisitorInfoResponse extends BaseIntegrationResponse {
    private AlienVisitorInfoResult result;
}
