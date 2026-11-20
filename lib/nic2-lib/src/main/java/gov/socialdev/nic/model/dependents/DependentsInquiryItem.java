package gov.socialdev.nic.model.dependents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependentsInquiryItem {
    @JsonProperty("idNumber")
    String idNumber;
    @JsonProperty("birthDateG")
    String birthDateG;
    String dependentSequenceNumber;
    String familyRelationCode;
    String familyRelationDescAr;
}
