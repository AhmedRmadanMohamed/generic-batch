package gov.socialdev.nic.model;

import gov.socialdev.nic.model.common.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Header {
    public String requestID;
    public Status status;
}
