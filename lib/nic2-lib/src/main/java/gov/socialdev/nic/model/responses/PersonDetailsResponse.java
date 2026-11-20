package gov.socialdev.nic.model.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.socialdev.nic.model.common.Header;
import gov.socialdev.nic.model.common.PersonData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode()
@AllArgsConstructor
@Data
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonDetailsResponse  {
    public Header header;
    public PersonData result;
}
