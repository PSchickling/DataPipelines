package de.schiggo.transformer.pipelinemanagement.service.extern;

import java.util.Map;

public interface PipelineGroupExecutor {
    String generateExecutionData(Map<Long, String> groupAttributes);

}
