package gov.socialdev.nic.model.dependents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependentsInquiryResult {
    @JsonProperty("listPersonDependents")
    List<DependentsInquiryItem> listPersonDependents;
    @JsonProperty("idNumber")
    String idNumber;
}
