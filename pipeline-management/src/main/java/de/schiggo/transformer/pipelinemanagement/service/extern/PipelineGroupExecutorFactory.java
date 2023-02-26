package de.schiggo.transformer.pipelinemanagement.service.extern;

import java.util.Optional;

public interface PipelineGroupExecutorFactory {

    Optional<PipelineGroupExecutor> fromName(String name);

}
