package gov.csr.generic.batch.procrssor;

import gov.csr.generic.batch.entity.PersonBasicInfoEntity;
import gov.csr.generic.batch.mapper.PersonBasicInfoMapper;
import gov.socialdev.nic.exception.InvalidNICResponseException;
import gov.socialdev.nic.exception.ServiceUnavailableException;
import gov.socialdev.nic.model.common.PersonData;
import gov.socialdev.nic.model.requests.PersonDetailsRequest;
import gov.socialdev.nic.model.responses.PersonDetailsResponse;
import gov.socialdev.nic.service.NICIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonBasicInfoProcessor {

    private final NICIntegrationService nicIntegrationService;
    private final PersonBasicInfoMapper personBasicInfoMapper;

    public PersonBasicInfoEntity process(PersonBasicInfoEntity entity) {
        String socialId = entity.getSocialId();

        try {
            PersonDetailsRequest request = PersonDetailsRequest.builder()
                    .socialId(socialId)
                    .birthDate(entity.getBirthDate())
                    .operatorSocialId(entity.getOperatorSocialId())
                    .build();

            PersonDetailsResponse response = nicIntegrationService.getPersonDetails(request);
            PersonData result = response == null ? null : response.getResult();
            if (result == null) {
                return mark(entity, 2, "Empty response result");
            }

            personBasicInfoMapper.updateEntityFromDto(result, entity);
            return mark(entity, 1, null);
        } catch (InvalidNICResponseException e) {
            log.error("Invalid NIC response for socialId {}: {}", socialId, e.getMessage());
            return mark(entity, 3, messageOrDefault(e, InvalidNICResponseException.DEFAULT_MESSAGE));
        } catch (ServiceUnavailableException e) {
            log.error("NIC service unavailable for socialId {}", socialId, e);
            return mark(entity, 3, ServiceUnavailableException.DEFAULT_MESSAGE);
        } catch (Exception e) {
            log.error("Unexpected error processing socialId {}: {}", socialId, e.getMessage(), e);
            return mark(entity, 3, "Processing error: " + e.getMessage());
        }
    }

    private String messageOrDefault(Exception exception, String defaultMessage) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? defaultMessage
                : exception.getMessage();
    }

    private PersonBasicInfoEntity mark(PersonBasicInfoEntity entity, int status, String errorMessage) {
        entity.setIsUpdated(status);
        entity.setErrorMessage(limit(errorMessage));
        entity.setModifiedBy(Thread.currentThread().getName());
        return entity;
    }

    private String limit(String message) {
        if (message == null) {
            return null;
        }
        return message.substring(0, Math.min(message.length(), 200));
    }
}
