package gov.csr.generic.batch.repository;

import gov.csr.generic.batch.entity.BatchJobConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface BatchJobConfigRepository extends JpaRepository<BatchJobConfig, Long>, JpaSpecificationExecutor<BatchJobConfig> {
    Optional<BatchJobConfig> findByJobName(String jobName);
    boolean existsByJobNameAndNeedRestartTrue(String jobName);
    List<BatchJobConfig> findByNeedRestartTrueAndRunningFalse();
}
