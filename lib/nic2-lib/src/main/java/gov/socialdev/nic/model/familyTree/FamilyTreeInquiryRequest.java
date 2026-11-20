package gov.socialdev.nic.model.familyTree;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FamilyTreeInquiryRequest {
    String nationalId;
    String birthDate;
    String operatorId;
}
