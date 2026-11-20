package gov.socialdev.nic.model.vehiclesInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehiclesInfoRequest {
    String socialId;
    String birthDate;
    String operatorSocialId;
}