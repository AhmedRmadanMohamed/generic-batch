package gov.csr.generic.batch.reader;

import gov.csr.generic.batch.entity.PersonBasicInfoEntity;
import gov.csr.generic.batch.util.PersonBasicInfoTablePartitioner;
import gov.csr.generic.batch.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.OraclePagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class PersonBasicInfoReaderConfig {

    private final DataSource dataSource;

    @Bean
    public Partitioner personBasicInfoHighPriorityTablePartitioner() {
        return new PersonBasicInfoTablePartitioner(dataSource, 1);
    }

    @Bean
    public Partitioner personBasicInfoLowPriorityTablePartitioner() {
        return new PersonBasicInfoTablePartitioner(dataSource, 0);
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<PersonBasicInfoEntity> personBasicInfoHighPriorityReader(
            @Value("#{jobParameters['pageSize']}") Long pageSize,
            @Value("#{stepExecutionContext['startRow']}") Long startRow,
            @Value("#{stepExecutionContext['endRow']}") Long endRow) {
        return buildReader("highPriorityPersonBasicInfoReader", 1, pageSize, startRow, endRow);
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<PersonBasicInfoEntity> personBasicInfoLowPriorityReader(
            @Value("#{jobParameters['pageSize']}") Long pageSize,
            @Value("#{stepExecutionContext['startRow']}") Long startRow,
            @Value("#{stepExecutionContext['endRow']}") Long endRow) {
        return buildReader("lowPriorityPersonBasicInfoReader", 0, pageSize, startRow, endRow);
    }

    private JdbcPagingItemReader<PersonBasicInfoEntity> buildReader(
            String readerName,
            int priority,
            Long pageSize,
            Long startRow,
            Long endRow) {

        log.info("Start read PersonBasicInfo priority [{}] at time [{}]",
                priority, Utils.getDateTime(System.currentTimeMillis()));

        String fromClause = String.format(
                "(SELECT a.*, ROW_NUMBER() OVER (ORDER BY ID ASC) AS RNUM "
                        + "FROM API_NIC_PERSON_BASIC_INFO a "
                        + "WHERE IS_ACTIVE = 1 AND IS_UPDATED = 0 AND PRIORITY = %d)",
                priority
        );
        String whereClause = String.format(
                "WHERE RNUM >= %d AND RNUM <= %d",
                startRow,
                endRow
        );

        OraclePagingQueryProvider queryProvider = new OraclePagingQueryProvider();
        queryProvider.setSelectClause(
                """
                SELECT
                    ID,
                    SOCIAL_ID,
                    BIRTH_DATE,
                    OPERATOR_SOCIAL_ID,
                    IS_ACTIVE,
                    IS_UPDATED,
                    PRIORITY,
                    ERROR_MESSAGE
                """
        );
        queryProvider.setFromClause(fromClause);
        queryProvider.setWhereClause(whereClause);
        queryProvider.setSortKeys(Map.of("ID", Order.ASCENDING));

        return new JdbcPagingItemReaderBuilder<PersonBasicInfoEntity>()
                .name(readerName)
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .pageSize(pageSize.intValue())
                .rowMapper((rs, rowNum) -> {
                    PersonBasicInfoEntity entity = new PersonBasicInfoEntity();
                    entity.setId(rs.getLong("ID"));
                    entity.setSocialId(rs.getString("SOCIAL_ID"));
                    entity.setBirthDate(rs.getString("BIRTH_DATE"));
                    entity.setOperatorSocialId(rs.getString("OPERATOR_SOCIAL_ID"));
                    entity.setIsActive(rs.getInt("IS_ACTIVE"));
                    entity.setIsUpdated(rs.getInt("IS_UPDATED"));
                    entity.setPriority(rs.getInt("PRIORITY"));
                    entity.setErrorMessage(rs.getString("ERROR_MESSAGE"));
                    return entity;
                })
                .build();
    }

}
