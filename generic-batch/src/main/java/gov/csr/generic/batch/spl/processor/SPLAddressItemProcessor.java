package gov.csr.generic.batch.spl.processor;

import gov.csr.generic.batch.spl.model.SPLAddressResult;
import gov.csr.generic.batch.spl.model.SPLPersonInput;
import gov.csr.generic.batch.spl.model.SPLProcessingStatus;
import gov.socialdev.spl.exception.InvalidSPLResponseException;
import gov.socialdev.spl.exception.SPLNoRecordFoundException;
import gov.socialdev.spl.exception.SPLServiceUnavailableException;
import gov.socialdev.spl.model.address.AddressDTO;
import gov.socialdev.spl.service.SPLIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Calls SPL for one person and converts both business and technical outcomes
 * into an item result. Expected per-person outcomes do not fail the whole step.
 */
@Slf4j
@Component("splAddressItemProcessor")
public class SPLAddressItemProcessor implements ItemProcessor<SPLPersonInput, SPLAddressResult> {

    private static final int MAX_ERROR_MESSAGE_LENGTH = 450;

    private final SPLIntegrationService splIntegrationService;

    public SPLAddressItemProcessor(SPLIntegrationService splIntegrationService) {
        this.splIntegrationService = Objects.requireNonNull(
                splIntegrationService, "splIntegrationService must not be null");
    }

    @Override
    public SPLAddressResult process(SPLPersonInput item) {
        Objects.requireNonNull(item, "item must not be null");

        try {
            List<AddressDTO> addresses = splIntegrationService.getAddressList(item.socialId());
            if (addresses == null || addresses.isEmpty()) {
                log.info("SPL returned an empty address list for sourceId {}", item.id());
                return SPLAddressResult.emptyResult(item.id(), item.socialId());
            }

            return SPLAddressResult.success(item.id(), item.socialId(), addresses);
        } catch (SPLNoRecordFoundException e) {
            log.info("SPL has no address record for sourceId {}", item.id());
            return SPLAddressResult.noRecordFound(
                    item.id(),
                    item.socialId(),
                    messageOrDefault(e, SPLNoRecordFoundException.DEFAULT_MESSAGE));
        } catch (SPLServiceUnavailableException e) {
            log.error("SPL service unavailable while processing sourceId {}", item.id(), e);
            return SPLAddressResult.failure(
                    item.id(),
                    item.socialId(),
                    SPLProcessingStatus.SERVICE_UNAVAILABLE,
                    messageOrDefault(e, SPLServiceUnavailableException.DEFAULT_MESSAGE));
        } catch (InvalidSPLResponseException e) {
            log.error("Invalid SPL response while processing sourceId {}", item.id(), e);
            return SPLAddressResult.failure(
                    item.id(),
                    item.socialId(),
                    SPLProcessingStatus.INVALID_RESPONSE,
                    messageOrDefault(e, InvalidSPLResponseException.DEFAULT_MESSAGE));
        } catch (Exception e) {
            log.error("Unexpected error while processing SPL sourceId {}", item.id(), e);
            return SPLAddressResult.failure(
                    item.id(),
                    item.socialId(),
                    SPLProcessingStatus.FAILED,
                    limit("Processing error: " + messageOrDefault(e, "Unexpected error")));
        }
    }

    private String messageOrDefault(Exception exception, String defaultMessage) {
        String message = exception.getMessage();
        return limit(message == null || message.isBlank() ? defaultMessage : message);
    }

    private String limit(String message) {
        if (message == null) {
            return null;
        }
        return message.substring(0, Math.min(message.length(), MAX_ERROR_MESSAGE_LENGTH));
    }
}
