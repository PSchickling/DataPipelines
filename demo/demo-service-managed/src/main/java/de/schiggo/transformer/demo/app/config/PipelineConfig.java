package de.schiggo.transformer.demo.app.config;

import de.schiggo.transformer.demo.persistence.source.repo.AddressRepository;
import de.schiggo.transformer.demo.persistence.source.repo.PersonRepository;
import de.schiggo.transformer.demo.persistence.target.repo.PersonReportingRepository;
import de.schiggo.transformer.demo.pipeline.PipelineExecutorFactoryImpl;
import de.schiggo.transformer.demo.pipeline.PipelineGroupExecutorFactoryImpl;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutorFactory;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PipelineConfig {

    @Bean
    public PipelineGroupExecutorFactory pipelineGroupExecutorFactory() {
        return new PipelineGroupExecutorFactoryImpl();
    }

    @Bean
    public PipelineExecutorFactory pipelineExecutorFactory(PersonRepository personRepository, AddressRepository addressRepository, PersonReportingRepository personReportingRepository) {
        return new PipelineExecutorFactoryImpl(personRepository, addressRepository, personReportingRepository);
    }
}
