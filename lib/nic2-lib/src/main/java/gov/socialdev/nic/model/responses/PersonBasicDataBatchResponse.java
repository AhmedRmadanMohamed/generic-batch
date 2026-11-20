package gov.socialdev.nic.model.responses;

import gov.socialdev.nic.model.common.PersonBasicData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonBasicDataBatchResponse {
    private List<PersonBasicData> validMembers;
}
