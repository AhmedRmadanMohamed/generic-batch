package gov.csr.generic.batch.spl.processor;

import gov.csr.generic.batch.spl.model.SPLAddressResult;
import gov.csr.generic.batch.spl.model.SPLPersonInput;
import gov.socialdev.spl.service.SPLIntegrationService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Enriches one source person with the complete address list returned by spl-lib.
 *
 * SPL exceptions are deliberately propagated to Spring Batch. Retry and
 * record-status policies are not encoded here until their business rules are agreed.
 */
@Component("splAddressItemProcessor")
public class SPLAddressItemProcessor implements ItemProcessor<SPLPersonInput, SPLAddressResult> {

    private final SPLIntegrationService splIntegrationService;

    public SPLAddressItemProcessor(SPLIntegrationService splIntegrationService) {
        this.splIntegrationService = Objects.requireNonNull(
                splIntegrationService, "splIntegrationService must not be null");
    }

    @Override
    public SPLAddressResult process(SPLPersonInput item) {
        Objects.requireNonNull(item, "item must not be null");

        return new SPLAddressResult(
                item.id(),
                item.socialId(),
                splIntegrationService.getAddressList(item.socialId())
        );
    }
}
