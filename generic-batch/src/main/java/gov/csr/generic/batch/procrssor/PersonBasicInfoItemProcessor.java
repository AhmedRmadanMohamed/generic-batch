package gov.csr.generic.batch.procrssor;

import gov.csr.generic.batch.entity.PersonBasicInfoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component("personBasicInfoItemProcessor")
@RequiredArgsConstructor
@Slf4j
public class PersonBasicInfoItemProcessor
        implements ItemProcessor<PersonBasicInfoEntity, PersonBasicInfoEntity> {

    private final PersonBasicInfoProcessor personBasicInfoProcessor;

    @Override
    public PersonBasicInfoEntity process(PersonBasicInfoEntity item) {
        log.info("Thread [{}] processing ID [{}]", Thread.currentThread().getName(), item.getId());
        return personBasicInfoProcessor.process(item);
    }
}
