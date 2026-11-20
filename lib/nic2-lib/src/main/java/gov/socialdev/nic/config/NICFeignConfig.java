package gov.socialdev.nic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class NICFeignConfig {

    @Bean
    public ErrorDecoder nicErrorDecoder() {
        return new NICErrorDecoder(new ObjectMapper());
    }
}
