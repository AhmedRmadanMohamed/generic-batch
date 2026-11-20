package gov.socialdev.nic.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AbroadQualification {
    public String instituteId;
    public String instituteName;
    public String instituteNameEn;
    public String collegeId;
    public String collegeName;
    public String sectionName;
    public String majorId;
    public String majorName;
    public String majorNameEn;
    public String minorName;
    public String studyPeriod;
    public String studyModeId;
    public String studyModeName;
    public String studyModeNameEn;
    @JsonProperty("GPATypeId")
    public String gPATypeId;
    @JsonProperty("GPATypeName")
    public String gPATypeName;
    @JsonProperty("GPA")
    public String gPA;
    public String ratingId;
    public String ratingName;
    public String gradutionYear;
    public String equationDecision;
    public String degreeId;
    public String degreeName;
    public String degreeNameEn;
    public String graduationDate;
    public String hijriGraduationDate;
    public String countryOfStudy;
    public String countryOfStudyEn;
    public String universitySaudiMajorName;
    public String degreeTypeName;
    public String equalizedDegree;
    public String decisionText;
    public String certificateTitle;
    public String certificateTitleEn;
    public String equationDecisionText;
    public String saudiMajorCode;
    public String saudiMajorName;
    public String saudiEducationLevelCode;
    public String saudiEducationLevelName;
}
