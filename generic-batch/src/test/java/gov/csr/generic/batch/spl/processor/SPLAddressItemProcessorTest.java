package gov.csr.generic.batch.spl.processor;

import gov.csr.generic.batch.spl.model.SPLAddressResult;
import gov.csr.generic.batch.spl.model.SPLPersonInput;
import gov.socialdev.spl.model.address.AddressDTO;
import gov.socialdev.spl.service.SPLIntegrationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SPLAddressItemProcessorTest {

    private final SPLIntegrationService splIntegrationService = mock(SPLIntegrationService.class);
    private final SPLAddressItemProcessor processor = new SPLAddressItemProcessor(splIntegrationService);

    @Test
    void preservesAllAddressesAndTheSourceIdentity() {
        AddressDTO first = AddressDTO.builder().id(101L).cityNameEn("Riyadh").build();
        AddressDTO second = AddressDTO.builder().id(102L).cityNameEn("Jeddah").build();
        when(splIntegrationService.getAddressList("1234567890"))
                .thenReturn(List.of(first, second));

        SPLAddressResult result = processor.process(new SPLPersonInput(77L, "1234567890"));

        assertThat(result.sourceId()).isEqualTo(77L);
        assertThat(result.socialId()).isEqualTo("1234567890");
        assertThat(result.addresses()).containsExactly(first, second);
    }

    @Test
    void preservesAnEmptyAddressList() {
        when(splIntegrationService.getAddressList("1234567890")).thenReturn(List.of());

        SPLAddressResult result = processor.process(new SPLPersonInput(77L, "1234567890"));

        assertThat(result.addresses()).isEmpty();
    }

    @Test
    void propagatesServiceFailuresToSpringBatch() {
        when(splIntegrationService.getAddressList("1234567890"))
                .thenThrow(new IllegalStateException("SPL unavailable"));

        assertThatThrownBy(() -> processor.process(new SPLPersonInput(77L, "1234567890")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("SPL unavailable");
    }
}
