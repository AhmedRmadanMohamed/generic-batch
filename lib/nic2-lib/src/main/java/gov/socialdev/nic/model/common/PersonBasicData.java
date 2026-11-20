package gov.socialdev.nic.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonBasicData {
    private String nationalId;
    private String fullName;
    private String firstName;
    private String lastName;
    private String birthDateHijri;
    private String mobile;
    private String email;
    // Added gender to support NIC person basic details response
    private String gender;
    private boolean registered = false;
}
