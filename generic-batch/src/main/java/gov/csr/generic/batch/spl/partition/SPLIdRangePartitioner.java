package gov.csr.generic.batch.spl.partition;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Splits an inclusive, already-resolved ID range into non-overlapping partitions.
 *
 * The caller supplies MIN_ID and MAX_ID after the preparation procedure has run.
 * No source table or SQL predicate is assumed in this class.
 */
public final class SPLIdRangePartitioner implements Partitioner {

    public static final String MIN_ID_KEY = "minId";
    public static final String MAX_ID_KEY = "maxId";
    public static final String PARTITION_SIZE_KEY = "partitionSize";

    private final Long minId;
    private final Long maxId;

    public SPLIdRangePartitioner(Long minId, Long maxId) {
        if ((minId == null) != (maxId == null)) {
            throw new IllegalArgumentException("minId and maxId must either both be present or both be null");
        }
        if (minId != null && minId > maxId) {
            throw new IllegalArgumentException("minId must be less than or equal to maxId");
        }
        this.minId = minId;
        this.maxId = maxId;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        if (gridSize < 1) {
            throw new IllegalArgumentException("gridSize must be greater than zero");
        }

        if (minId == null) {
            return Map.of();
        }

        long totalRows = Math.addExact(Math.subtractExact(maxId, minId), 1L);
        int partitionCount = (int) Math.min((long) gridSize, totalRows);
        long baseSize = totalRows / partitionCount;
        long remainder = totalRows % partitionCount;

        Map<String, ExecutionContext> partitions = new LinkedHashMap<>(partitionCount);
        long currentMin = minId;

        for (int index = 0; index < partitionCount; index++) {
            long size = baseSize + (index < remainder ? 1L : 0L);
            long currentMax = Math.addExact(currentMin, size - 1L);

            ExecutionContext context = new ExecutionContext();
            context.putLong(MIN_ID_KEY, currentMin);
            context.putLong(MAX_ID_KEY, currentMax);
            context.putLong(PARTITION_SIZE_KEY, size);
            partitions.put("splPartition" + index, context);

            if (index < partitionCount - 1) {
                currentMin = Math.addExact(currentMax, 1L);
            }
        }

        return partitions;
    }
}
