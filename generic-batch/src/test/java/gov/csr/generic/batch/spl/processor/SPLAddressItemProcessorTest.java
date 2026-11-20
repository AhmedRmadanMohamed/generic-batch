package gov.csr.generic.batch.spl.processor;

import gov.csr.generic.batch.spl.model.SPLAddressResult;
import gov.csr.generic.batch.spl.model.SPLPersonInput;
import gov.csr.generic.batch.spl.model.SPLProcessingStatus;
import gov.socialdev.spl.exception.InvalidSPLResponseException;
import gov.socialdev.spl.exception.SPLNoRecordFoundException;
import gov.socialdev.spl.exception.SPLServiceUnavailableException;
import gov.socialdev.spl.model.address.AddressDTO;
import gov.socialdev.spl.service.SPLIntegrationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SPLAddressItemProcessorTest {

    private final SPLIntegrationService splIntegrationService = mock(SPLIntegrationService.class);
    private final SPLAddressItemProcessor processor = new SPLAddressItemProcessor(splIntegrationService);

    @Test
    void returnsAllAddressesOnSuccess() {
        AddressDTO first = AddressDTO.builder().id(101L).cityNameEn("Riyadh").build();
        AddressDTO second = AddressDTO.builder().id(102L).cityNameEn("Jeddah").build();
        when(splIntegrationService.getAddressList("1234567890"))
                .thenReturn(List.of(first, second));

        SPLAddressResult result = processor.process(new SPLPersonInput(77L, "1234567890"));

        assertThat(result.sourceId()).isEqualTo(77L);
        assertThat(result.socialId()).isEqualTo("1234567890");
        assertThat(result.addresses()).containsExactly(first, second);
        assertThat(result.status()).isEqualTo(SPLProcessingStatus.SUCCESS);
        assertThat(result.errorMessage()).isNull();
    }

    @Test
    void marksSuccessfulEmptyAddressListAsEmptyResult() {
        when(splIntegrationService.getAddressList("1234567890")).thenReturn(List.of());

        SPLAddressResult result = processor.process(new SPLPersonInput(77L, "1234567890"));

        assertThat(result.addresses()).isEmpty();
        assertThat(result.status()).isEqualTo(SPLProcessingStatus.EMPTY_RESULT);
        assertThat(result.errorMessage()).isNull();
    }

    @Test
    void marksNoRecordFoundAsBusinessOutcomeWithoutThrowing() {
        when(splIntegrationService.getAddressList("1234567890"))
                .thenThrow(new SPLNoRecordFoundException());

        SPLAddressResult result = processor.process(new SPLPersonInput(77L, "1234567890"));

        assertThat(result.sourceId()).isEqualTo(77L);
        assertThat(result.addresses()).isEmpty();
        assertThat(result.status()).isEqualTo(SPLProcessingStatus.NO_RECORD_FOUND);
        assertThat(result.errorMessage()).isEqualTo(SPLNoRecordFoundException.DEFAULT_MESSAGE);
    }

    @Test
    void marksServiceUnavailableAsTechnicalFailureWithoutThrowing() {
        when(splIntegrationService.getAddressList("1234567890"))
                .thenThrow(new SPLServiceUnavailableException());

        SPLAddressResult result = processor.process(new SPLPersonInput(77L, "1234567890"));

        assertThat(result.status()).isEqualTo(SPLProcessingStatus.SERVICE_UNAVAILABLE);
        assertThat(result.errorMessage()).isEqualTo(SPLServiceUnavailableException.DEFAULT_MESSAGE);
    }

    @Test
    void marksInvalidResponseAsTechnicalFailureWithoutThrowing() {
        when(splIntegrationService.getAddressList("1234567890"))
                .thenThrow(new InvalidSPLResponseException());

        SPLAddressResult result = processor.process(new SPLPersonInput(77L, "1234567890"));

        assertThat(result.status()).isEqualTo(SPLProcessingStatus.INVALID_RESPONSE);
        assertThat(result.errorMessage()).isEqualTo(InvalidSPLResponseException.DEFAULT_MESSAGE);
    }

    @Test
    void marksUnexpectedExceptionsAsFailedWithoutThrowing() {
        when(splIntegrationService.getAddressList("1234567890"))
                .thenThrow(new IllegalStateException("Unexpected failure"));

        SPLAddressResult result = processor.process(new SPLPersonInput(77L, "1234567890"));

        assertThat(result.status()).isEqualTo(SPLProcessingStatus.FAILED);
        assertThat(result.errorMessage()).isEqualTo("Processing error: Unexpected failure");
    }

    @Test
    void truncatesLongErrorMessagesToTheExistingErrorMessageLimit() {
        String longMessage = "x".repeat(600);
        when(splIntegrationService.getAddressList("1234567890"))
                .thenThrow(new IllegalStateException(longMessage));

        SPLAddressResult result = processor.process(new SPLPersonInput(77L, "1234567890"));

        assertThat(result.status()).isEqualTo(SPLProcessingStatus.FAILED);
        assertThat(result.errorMessage()).hasSize(450);
    }
}
