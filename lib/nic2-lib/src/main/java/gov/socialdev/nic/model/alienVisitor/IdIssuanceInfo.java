package gov.socialdev.nic.model.alienVisitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdIssuanceInfo {
    String issueDate;
    String expirationDate;
    CodeDescription issuePlace;
}
