package gov.csr.generic.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "gov.socialdev.nic.feign")
@ComponentScan(basePackages = {"gov.csr.generic.batch", "gov.socialdev.nic.service"})
@EnableJpaRepositories(basePackages = "gov.csr.generic.batch.repository")
@EntityScan(basePackages = "gov.csr.generic.batch.entity")
public class GenericBatch {

    public static void main(String[] args) {
        SpringApplication.run(GenericBatch.class, args);
    }
}
