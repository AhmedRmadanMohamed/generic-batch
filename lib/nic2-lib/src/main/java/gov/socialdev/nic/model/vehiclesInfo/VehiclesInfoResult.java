package gov.socialdev.nic.model.vehiclesInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiclesInfoResult {
    Integer hasVehicle;
    List<VehicleItem> vehiclesList;
    String requestTimestamp;
    String responseTimestamp;
}