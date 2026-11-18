package gov.csr.generic.batch.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PersonBasicInfoTablePartitioner implements Partitioner {

    private static final String TABLE_NAME = "API_NIC_PERSON_BASIC_INFO";

    private final JdbcTemplate jdbcTemplate;
    private final int priority;

    public PersonBasicInfoTablePartitioner(DataSource dataSource, int priority) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.priority = priority;
    }

    PersonBasicInfoTablePartitioner(JdbcTemplate jdbcTemplate, int priority) {
        this.jdbcTemplate = jdbcTemplate;
        this.priority = priority;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME
                + " WHERE IS_ACTIVE = 1 AND IS_UPDATED = 0 AND PRIORITY = ?";

        Long totalRows = jdbcTemplate.queryForObject(query, Long.class, priority);

        if (totalRows == null || totalRows == 0) {
            log.info("No records found for priority {} in {}", priority, TABLE_NAME);
            return new HashMap<>();
        }

        int actualGridSize = (int) Math.min(Math.max(gridSize, 1), totalRows);
        long rowsPerPartition = totalRows / actualGridSize;
        long remainder = totalRows % actualGridSize;

        Map<String, ExecutionContext> partitions = new HashMap<>();
        long currentStartRow = 1;

        for (int i = 0; i < actualGridSize; i++) {
            long partitionSize = rowsPerPartition + (i < remainder ? 1 : 0);
            long currentEndRow = currentStartRow + partitionSize - 1;

            ExecutionContext context = new ExecutionContext();
            context.putLong("startRow", currentStartRow);
            context.putLong("endRow", currentEndRow);
            context.putLong("partitionSize", partitionSize);
            context.putInt("priority", priority);
            partitions.put("partition" + i, context);

            log.info("Partition[{}] -> Rows: {} to {}, Priority: {}",
                    i, currentStartRow, currentEndRow, priority);
            currentStartRow = currentEndRow + 1;
        }

        log.info("Created {} partitions for {} priority [{}]",
                partitions.size(), TABLE_NAME, priority);
        return partitions;
    }
}
