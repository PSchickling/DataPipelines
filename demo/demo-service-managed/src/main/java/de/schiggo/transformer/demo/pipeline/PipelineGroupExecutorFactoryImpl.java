package de.schiggo.transformer.demo.pipeline;

import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutor;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutorFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class PipelineGroupExecutorFactoryImpl implements PipelineGroupExecutorFactory {

    /*public PipelineGroupExecutorFactoryImpl(PipelineManagerServiceImpl pipelineManagerService){
        pipelineManagerService.addPipelineGroup("group1", "", "", new HashMap<>());
    }*/

    @Override
    public Optional<PipelineGroupExecutor> fromName(String name) {
        return Optional.of(new PipelineGroupExecutor() {
            @Override
            public String generateExecutionData(Map<Long, String> groupAttributes) {
                return null;
            }
        });
    }
}
