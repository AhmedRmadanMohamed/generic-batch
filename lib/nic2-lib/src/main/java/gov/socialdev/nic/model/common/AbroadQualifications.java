package gov.socialdev.nic.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AbroadQualifications {
    public String status;
    public String description;
    public Qualifications qualifications;
}
