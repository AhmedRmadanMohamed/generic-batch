package gov.csr.generic.batch.spl.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.SynchronousQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SPLTaskExecutorFactoryTest {

    private final SPLTaskExecutorFactory factory = new SPLTaskExecutorFactory();

    @Test
    void createsFixedSizeBoundedPoolWithNoBacklogQueue() {
        ThreadPoolTaskExecutor executor = factory.create("splAddressJob", 4);
        try {
            assertThat(executor.getCorePoolSize()).isEqualTo(4);
            assertThat(executor.getMaxPoolSize()).isEqualTo(4);
            assertThat(executor.getThreadNamePrefix()).isEqualTo("spl-splAddressJob-worker-");
            assertThat(executor.getThreadPoolExecutor().getQueue())
                    .isInstanceOf(SynchronousQueue.class);
        } finally {
            executor.destroy();
        }
    }

    @Test
    void sanitizesJobNameInWorkerThreadPrefix() {
        ThreadPoolTaskExecutor executor = factory.create("spl.address/job", 2);
        try {
            assertThat(executor.getThreadNamePrefix()).isEqualTo("spl-spl-address-job-worker-");
        } finally {
            executor.destroy();
        }
    }

    @Test
    void rejectsInvalidThreadCount() {
        assertThatThrownBy(() -> factory.create("splAddressJob", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("threadCount must be greater than zero");
    }

    @Test
    void rejectsBlankJobName() {
        assertThatThrownBy(() -> factory.create("  ", 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("jobName must not be blank");
    }
}
