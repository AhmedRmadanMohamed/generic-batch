package gov.csr.generic.batch.spl.config;

import gov.socialdev.spl.feign.SPLClient;
import gov.socialdev.spl.service.SPLIntegrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = SPLIntegrationContextTest.TestApplication.class,
        properties = "spring.cloud.openfeign.client.config.saudi-post.url=http://127.0.0.1:1"
)
class SPLIntegrationContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void registersSplIntegrationConfiguration() {
        assertThat(applicationContext.getBean(SPLIntegrationConfiguration.class))
                .isNotNull();
    }

    @Test
    void registersSplFeignClientAndIntegrationServiceWithoutCallingTheRemoteApi() {
        assertThat(applicationContext.getBean(SPLClient.class))
                .isNotNull();
        assertThat(applicationContext.getBean(SPLIntegrationService.class))
                .isNotNull();
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            JpaRepositoriesAutoConfiguration.class,
            BatchAutoConfiguration.class
    })
    @Import(SPLIntegrationConfiguration.class)
    static class TestApplication {
    }
}
