package gov.csr.generic.batch.spl.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(basePackages = "gov.socialdev.spl.feign")
@ComponentScan(basePackages = "gov.socialdev.spl.service")
public class SPLIntegrationConfiguration {
}
