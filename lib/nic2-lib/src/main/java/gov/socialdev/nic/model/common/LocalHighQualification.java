package gov.socialdev.nic.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LocalHighQualification {
    public String instituteId;
    public String instituteName;
    public String collegeId;
    public String collegeName;
    public String sectionName;
    public String majorId;
    public String majorName;
    public String minorName;
    public String studyPeriod;
    public String studyModeId;
    public String studyModeName;
    @JsonProperty("GPATypeId")
    public String gPATypeId;
    @JsonProperty("GPATypeName")
    public String gPATypeName;
    @JsonProperty("GPA")
    public String gPA;
    public String ratingId;
    public String ratingName;
    public String gradutionYear;
    public String graduationDate;
    public String hijriGraduationDate;
    public String degreeId;
    public String degreeName;
    public String universitySaudiMajorName;
    public String degreeTypeName;
    public String mobileNo;
    public String saudiMajorCode;
    public String saudiMajorName;
    public String saudiEducationLevelCode;
    public String saudiEducationLevelName;
}
