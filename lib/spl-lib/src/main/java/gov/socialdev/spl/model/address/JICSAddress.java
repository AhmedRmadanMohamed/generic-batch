package gov.socialdev.spl.model.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JICSAddress {
    private String regionId;
    private String regionName;
    private String cityId;
    private String cityName;
}
