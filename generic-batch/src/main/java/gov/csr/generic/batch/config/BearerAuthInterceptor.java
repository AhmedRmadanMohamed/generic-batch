package gov.csr.generic.batch.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.repository.BatchJobConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BearerAuthInterceptor implements RequestInterceptor {

    private final BatchJobConfigRepository batchJobConfigRepository;
    private final GenericBatchProperties properties;

    @Override
    public void apply(RequestTemplate template) {
        BatchJobConfig config = batchJobConfigRepository
                .findByJobName(properties.getJobName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No BatchJobConfig found for job " + properties.getJobName()
                        )
                );

        if (config.isBearer()) {
            throw new IllegalStateException("personBasicInfo must use non-bearer NIC credentials");
        }

        if ("NIC".equalsIgnoreCase(template.feignTarget().name())) {
            template.header("X-HRSD-Client-Id", config.getClientId());
            template.header("X-HRSD-Client-Secret", config.getClientSecret());
            template.header("Request-ID", UUID.randomUUID().toString());
            template.header("Accept", "application/json");
            template.header("x-validation-report", "true");
        }
    }
}
