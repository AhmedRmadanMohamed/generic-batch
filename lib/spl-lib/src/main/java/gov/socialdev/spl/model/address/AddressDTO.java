package gov.socialdev.spl.model.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO implements Serializable {
    private Long id;
    private String fullName;

    private Integer buildingNumber;
    private Integer additionalNumber;
    private String zipcode;

    private String cityNameAr;
    private String cityNameEn;

    private String districtAreaAr;
    private String districtAreaEn;

    private String streetNameAr;
    private String streetNameEn;

    private String unitNumber;

    private String latitude;
    private String longitude;

    private String housingType;
    private String housingOwnership;

    @JsonProperty("JICSAddress")
    private JICSAddress jicsAddress;

    private String userId;
    private String sharedHousing;
    private String addressType;
    private String area;
}
