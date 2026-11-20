package gov.socialdev.nic.model.dependents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DependentsInquiryRequest {
    String idNumber;
    String birthDate;
    String operatorId;
}
