package gov.csr.generic.batch.service;

import gov.csr.generic.batch.config.GenericJobFactory;
import gov.csr.generic.batch.entity.BatchJobConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobResolver {

    private final GenericJobFactory jobFactory;

    public Job  resolveJob(BatchJobConfig config) {
        return jobFactory.buildJob(config);
    }
}
