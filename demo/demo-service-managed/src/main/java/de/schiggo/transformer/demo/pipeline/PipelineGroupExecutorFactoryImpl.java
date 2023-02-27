package de.schiggo.transformer.demo.pipeline;

import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutor;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutorFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class PipelineGroupExecutorFactoryImpl implements PipelineGroupExecutorFactory {

    @Override
    public Optional<PipelineGroupExecutor> fromClassName(String className) {
        return Optional.of(new PipelineGroupExecutor() {
            @Override
            public String generateExecutionData(Map<Long, String> groupAttributes) {
                return null;
            }
        });
    }
}
