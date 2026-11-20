package gov.socialdev.nic.model.requests;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDetailsRequest {
    @NotBlank
    public String socialId;
    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    public String birthDate;
    public String operatorSocialId;
}
