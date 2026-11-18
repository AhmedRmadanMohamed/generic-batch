package gov.csr.generic.batch.util;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PersonBasicInfoTablePartitionerTest {

    @Test
    void splitsTheActualRowCountAcrossPartitions() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), org.mockito.ArgumentMatchers.eq(Long.class),
                org.mockito.ArgumentMatchers.eq(1)))
                .thenReturn(10L);

        PersonBasicInfoTablePartitioner partitioner =
                new PersonBasicInfoTablePartitioner(jdbcTemplate, 1);

        Map<String, ExecutionContext> partitions = partitioner.partition(3);

        assertEquals(3, partitions.size());
        assertRange(partitions.get("partition0"), 1, 4, 4);
        assertRange(partitions.get("partition1"), 5, 7, 3);
        assertRange(partitions.get("partition2"), 8, 10, 3);
    }

    @Test
    void createsNoPartitionsWhenThereAreNoEligibleRows() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), org.mockito.ArgumentMatchers.eq(Long.class),
                org.mockito.ArgumentMatchers.eq(0)))
                .thenReturn(0L);

        PersonBasicInfoTablePartitioner partitioner =
                new PersonBasicInfoTablePartitioner(jdbcTemplate, 0);

        assertTrue(partitioner.partition(5).isEmpty());
    }

    private void assertRange(
            ExecutionContext context,
            long expectedStart,
            long expectedEnd,
            long expectedSize) {
        assertEquals(expectedStart, context.getLong("startRow"));
        assertEquals(expectedEnd, context.getLong("endRow"));
        assertEquals(expectedSize, context.getLong("partitionSize"));
    }
}
