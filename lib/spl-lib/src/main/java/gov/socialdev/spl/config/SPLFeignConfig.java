package gov.socialdev.spl.config;

import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;

public class SPLFeignConfig {

    @Bean
    public Decoder splDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SPLResponseDecoder(new SpringDecoder(messageConverters));
    }
}
