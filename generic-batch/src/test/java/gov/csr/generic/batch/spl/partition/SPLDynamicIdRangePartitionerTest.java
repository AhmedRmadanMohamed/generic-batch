package gov.csr.generic.batch.spl.partition;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SPLDynamicIdRangePartitionerTest {

    @Test
    void resolvesRangeWhenPartitioningStarts() {
        AtomicBoolean rangeResolved = new AtomicBoolean();
        SPLDynamicIdRangePartitioner partitioner = new SPLDynamicIdRangePartitioner(() -> {
            rangeResolved.set(true);
            return Optional.of(new SPLIdRange(100L, 105L));
        });

        assertThat(rangeResolved).isFalse();

        Map<String, ExecutionContext> partitions = partitioner.partition(2);

        assertThat(rangeResolved).isTrue();
        assertThat(partitions).hasSize(2);
        assertThat(partitions.get("splPartition0").getLong(SPLIdRangePartitioner.MIN_ID_KEY))
                .isEqualTo(100L);
        assertThat(partitions.get("splPartition0").getLong(SPLIdRangePartitioner.MAX_ID_KEY))
                .isEqualTo(102L);
        assertThat(partitions.get("splPartition1").getLong(SPLIdRangePartitioner.MIN_ID_KEY))
                .isEqualTo(103L);
        assertThat(partitions.get("splPartition1").getLong(SPLIdRangePartitioner.MAX_ID_KEY))
                .isEqualTo(105L);
    }

    @Test
    void returnsNoPartitionsWhenPreparedWorkSetIsEmpty() {
        SPLDynamicIdRangePartitioner partitioner =
                new SPLDynamicIdRangePartitioner(Optional::empty);

        assertThat(partitioner.partition(4)).isEmpty();
    }

    @Test
    void rejectsInvalidGridSizeBeforeResolvingRange() {
        AtomicBoolean rangeResolved = new AtomicBoolean();
        SPLDynamicIdRangePartitioner partitioner = new SPLDynamicIdRangePartitioner(() -> {
            rangeResolved.set(true);
            return Optional.empty();
        });

        assertThatThrownBy(() -> partitioner.partition(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gridSize must be greater than zero");
        assertThat(rangeResolved).isFalse();
    }
}
