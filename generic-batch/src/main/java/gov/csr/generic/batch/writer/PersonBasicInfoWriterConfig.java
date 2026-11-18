package gov.csr.generic.batch.writer;

import gov.csr.generic.batch.entity.PersonBasicInfoEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonBasicInfoWriterConfig {

    @Bean(name = "personBasicInfoWriter")
    @StepScope
    public JpaItemWriter<PersonBasicInfoEntity> personBasicInfoWriter(
            EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<PersonBasicInfoEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
