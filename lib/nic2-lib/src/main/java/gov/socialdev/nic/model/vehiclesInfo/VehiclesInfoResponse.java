package gov.socialdev.nic.model.vehiclesInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.socialdev.nic.model.BaseIntegrationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehiclesInfoResponse extends BaseIntegrationResponse {
    private VehiclesInfoResult result;
}