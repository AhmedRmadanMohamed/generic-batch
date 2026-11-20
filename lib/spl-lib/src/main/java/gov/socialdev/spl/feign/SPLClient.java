package gov.socialdev.spl.feign;

import gov.socialdev.spl.config.SPLFeignConfig;
import gov.socialdev.spl.model.response.SaudiPostResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "saudi-post", configuration = SPLFeignConfig.class)
public interface SPLClient {

    @GetMapping("/v1/person/{socialId}/national-address")
    @Retry(name = "splRetry")
    @CircuitBreaker(name = "splCircuitBreaker")
    SaudiPostResponse getAddresses(@PathVariable("socialId") String socialId);
}
