package gov.socialdev.nic.model.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class QualificationRequest {
    @NotBlank
    public String nationalId;
}
