package gov.socialdev.nic.model.familyTree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyTreeInquiryResult {
    String fatherSocialId;
    String motherSocialId;
    String fatherBirthDate;
    String motherBirthDate;
    String fFirstName;
    String fFatherName;
    String fGrandFatherName;
    String fFamilyName;
    String mFirstName;
    String mFatherName;
    String mGrandFatherName;
    String mFamilyName;
    String husbandID;
    String husbandBirthdate;
    List<WifeInfo> wivesList;
}
