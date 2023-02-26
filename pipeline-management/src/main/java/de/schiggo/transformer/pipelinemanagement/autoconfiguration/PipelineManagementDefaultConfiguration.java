package de.schiggo.transformer.pipelinemanagement.autoconfiguration;

import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutorFactory;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutorFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@ConditionalOnProperty(prefix = "pipeline-management", name = "enabled")
@Configuration
@ComponentScan(basePackages = {"de.schiggo.transformer.pipelinemanagement.service"})
public class PipelineManagementDefaultConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PipelineGroupExecutorFactory pipelineGroupExecutorFactory() {
        return id -> Optional.empty();
    }

    @Bean
    @ConditionalOnMissingBean
    public PipelineExecutorFactory pipelineExecutorFactory() {
        return (className, pipelineAttributes, pipelineGroupAttributes, processData) -> Optional.empty();
    }

}