package gov.socialdev.nic.model.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class PersonRelationDetailsRequest {
    @NotBlank
    private String socialId;
    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String mainBirthDate;
    @NotBlank
    private String subSocialId;
    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String subBirthDate;
    @NotBlank
    private String operatorSocialId;

}
