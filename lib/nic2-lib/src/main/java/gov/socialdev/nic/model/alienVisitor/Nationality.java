package gov.socialdev.nic.model.alienVisitor;

import gov.socialdev.nic.model.common.NationalityDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nationality {
    Integer code;
    NationalityDescription description;
}
