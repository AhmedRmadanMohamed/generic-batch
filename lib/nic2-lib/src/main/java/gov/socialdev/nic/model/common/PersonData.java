package gov.socialdev.nic.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@lombok.Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonData {
    public String socialId;
    public PersonName name;
    public Nationality nationality;
    public Religion religion;
    public Occupation occupation;
    public MaritalStatus maritalStatus;
    public Gender gender;
    public LifeStatus lifeStatus;
    public String requestTimestamp;
    public String responseTimestamp;
    public BirthDetails birthDetails;
}
