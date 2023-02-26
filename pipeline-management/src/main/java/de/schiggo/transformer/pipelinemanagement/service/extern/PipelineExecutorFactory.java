package de.schiggo.transformer.pipelinemanagement.service.extern;

import java.util.Map;
import java.util.Optional;

public interface PipelineExecutorFactory {

    Optional<PipelineExecutor> fromClassName(String className, Map<Long, String> pipelineAttributes, Map<Long, String> pipelineGroupAttributes, String processData);

}
