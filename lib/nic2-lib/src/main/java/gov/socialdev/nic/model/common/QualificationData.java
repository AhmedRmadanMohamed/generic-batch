package gov.socialdev.nic.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class QualificationData {
    public String nationalId;
    public String emailAddress;
    public String arabicFullName;
    public String englishFullName;
    public String nationalityName;
    public String nationalityId;
    public AbroadQualifications abroadQualifications;
    public LocalHighQualifications localHighQualifications;
}
