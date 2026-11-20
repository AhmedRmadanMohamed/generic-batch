package gov.socialdev.nic.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
@AllArgsConstructor
@Data
public class Qualifications {
    public ArrayList<AbroadQualification> abroadQualification;
    public ArrayList<LocalHighQualification> localHighQualification;
}
