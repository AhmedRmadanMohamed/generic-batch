package gov.csr.generic.batch.spl.partition;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Resolves the current SPL ID range when Spring Batch starts the master step.
 * Since the master step follows the preparation-procedure step, the range
 * lookup observes the prepared work set rather than a pre-procedure snapshot.
 */
public final class SPLDynamicIdRangePartitioner implements Partitioner {

    private final SPLIdRangeProvider rangeProvider;

    public SPLDynamicIdRangePartitioner(SPLIdRangeProvider rangeProvider) {
        this.rangeProvider = Objects.requireNonNull(rangeProvider, "rangeProvider must not be null");
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        if (gridSize < 1) {
            throw new IllegalArgumentException("gridSize must be greater than zero");
        }

        Optional<SPLIdRange> range = Objects.requireNonNull(
                rangeProvider.findRange(), "rangeProvider must return an Optional");

        return range.map(value ->
                        new SPLIdRangePartitioner(value.minId(), value.maxId()).partition(gridSize))
                .orElseGet(Map::of);
    }
}
