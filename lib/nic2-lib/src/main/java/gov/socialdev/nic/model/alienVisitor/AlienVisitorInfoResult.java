package gov.socialdev.nic.model.alienVisitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.socialdev.nic.model.common.Description;
import gov.socialdev.nic.model.common.MaritalStatus;
import gov.socialdev.nic.model.common.Religion;
import gov.socialdev.nic.model.common.Occupation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlienVisitorInfoResult {
    String iqamaId;
    String isIqamaIssuedFlag;
    String borderNumber;
    String activity;
    String isEnrolledFlg;

    NameInfo nameInfo;
    Gender gender;
    Nationality nationality;
    BirthInfo birthInfo;
    Occupation occupation;

    CodeDescription status;
    IdIssuanceInfo idIssuanceInfo;
    VisaInfo visaInfo;
    PassportInfo passportInfo;
    Religion religion;
    MaritalStatus maritalStatus;
    CodeDescription relationWithcurrentHoh;
    SponsorInfo sponsorInfo;
    FirmType firmType;
    Description travelStatus;
    DepartureDateInfo departureDateInfo;
    FileNumber fileNumber;

    String hasFinalVisaFlag;
    String isHoh;
    Integer iqamaType;
    String iqamaTypeDescAr;
    String iqamaTypeDescEn;
}
