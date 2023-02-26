package de.schiggo.transformer.demo.pipeline;

import de.schiggo.transformer.demo.persistence.source.repo.AddressRepository;
import de.schiggo.transformer.demo.persistence.source.repo.PersonRepository;
import de.schiggo.transformer.demo.persistence.target.repo.PersonReportingRepository;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutor;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PipelineExecutorFactoryImpl implements PipelineExecutorFactory {

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final PersonReportingRepository personReportingRepository;

    /*
        public PipelineExecutorFactoryImpl(PipelineManagerServiceImpl pipelineManagerService, PersonRepository personRepository, AddressRepository addressRepository, PersonReportingRepository personReportingRepository){
            this.personRepository = personRepository;
            this.addressRepository = addressRepository;
            this.personReportingRepository = personReportingRepository;

            pipelineManagerService.addPipeline("pipeline1", "", "", new HashMap<>(), "group1");
            pipelineManagerService.addPipeline("pipeline2", "", "", new HashMap<>(), "group1");
        }
    */
    @Override
    public Optional<PipelineExecutor> fromClassName(String className, Map<Long, String> pipelineAttributes, Map<Long, String> pipelineGroupAttributes, String processData) {
        if ("pipeline1".equals(className)) {
            Calendar validAt = Calendar.getInstance();
            return Optional.of(new SimplePersonPipeline(personRepository, addressRepository, personReportingRepository, validAt));
        } else if ("pipeline2".equals(className)) {
            return Optional.of(new SimplePersonPipeline2());
        }
        return Optional.empty();
    }
}
