package gov.socialdev.nic.model.alienVisitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisaInfo {
    String expiryDate;
    String isVisaUsed;
    String isVisaCanceled;
    String isVisaExpired;

    @JsonProperty("IssuePlaceInfo")
    VisaIssuePlaceInfo issuePlaceInfo;
}
