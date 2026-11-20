package gov.socialdev.nic.model.alienVisitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.socialdev.nic.model.common.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gender {
    Integer code;
    Description description;
}
