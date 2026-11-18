package gov.csr.generic.batch.service;

import gov.csr.generic.batch.entity.BatchJobConfig;
import gov.csr.generic.batch.repository.BatchJobConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobConfigService {

    private final BatchJobConfigRepository repository;

    public BatchJobConfig getJobConfig(String jobName) {
        return repository.findByJobName(jobName).orElseThrow(() -> new IllegalStateException("No job config found for job " + jobName));
    }
}
