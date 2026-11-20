package gov.socialdev.nic.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PersonRelationDetails {
    private String relationshipDescAr;
    private String relationshipDescEn;
    private String relationshipCode;

}
