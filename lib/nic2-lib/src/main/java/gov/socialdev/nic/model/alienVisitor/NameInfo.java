package gov.socialdev.nic.model.alienVisitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameInfo {
    String firstName;
    String fatherName;
    String grandFatherName;
    String familyName;
    String firstNameTr;
    String fatherNameTr;
    String grandFatherNameTr;
    String familyNameTr;
}
