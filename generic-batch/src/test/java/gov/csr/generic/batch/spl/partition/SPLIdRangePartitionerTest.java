package gov.csr.generic.batch.spl.partition;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SPLIdRangePartitionerTest {

    @Test
    void splitsInclusiveRangeEvenlyWithoutGapsOrOverlaps() {
        Map<String, ExecutionContext> partitions =
                new SPLIdRangePartitioner(1L, 10L).partition(3);

        assertThat(partitions).containsOnlyKeys("splPartition0", "splPartition1", "splPartition2");
        assertRange(partitions.get("splPartition0"), 1L, 4L, 4L);
        assertRange(partitions.get("splPartition1"), 5L, 7L, 3L);
        assertRange(partitions.get("splPartition2"), 8L, 10L, 3L);

        List<Long> allIds = partitions.values().stream()
                .flatMap(context -> java.util.stream.LongStream.rangeClosed(
                        context.getLong(SPLIdRangePartitioner.MIN_ID_KEY),
                        context.getLong(SPLIdRangePartitioner.MAX_ID_KEY)).boxed())
                .toList();

        assertThat(allIds).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        assertThat(allIds).doesNotHaveDuplicates();
    }

    @Test
    void neverCreatesMorePartitionsThanIds() {
        Map<String, ExecutionContext> partitions =
                new SPLIdRangePartitioner(42L, 42L).partition(8);

        assertThat(partitions).hasSize(1);
        assertRange(partitions.get("splPartition0"), 42L, 42L, 1L);
    }

    @Test
    void returnsNoPartitionsForAnEmptyPreparedRange() {
        assertThat(new SPLIdRangePartitioner(null, null).partition(4)).isEmpty();
    }

    @Test
    void rejectsInvalidGridSize() {
        assertThatThrownBy(() -> new SPLIdRangePartitioner(1L, 5L).partition(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gridSize must be greater than zero");
    }

    @Test
    void rejectsIncompleteOrReversedRanges() {
        assertThatThrownBy(() -> new SPLIdRangePartitioner(1L, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SPLIdRangePartitioner(5L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void assertRange(
            ExecutionContext context,
            long expectedMin,
            long expectedMax,
            long expectedSize) {
        assertThat(context.getLong(SPLIdRangePartitioner.MIN_ID_KEY)).isEqualTo(expectedMin);
        assertThat(context.getLong(SPLIdRangePartitioner.MAX_ID_KEY)).isEqualTo(expectedMax);
        assertThat(context.getLong(SPLIdRangePartitioner.PARTITION_SIZE_KEY)).isEqualTo(expectedSize);
    }
}
