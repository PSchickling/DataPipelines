package de.schiggo.transformer.pipelinemanagement.service.extern;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelineGroup;

import java.util.Optional;

/**
 * A {@link PipelineGroupExecutorFactory} is a factory to instantiate {@link PipelineGroupExecutor}s.
 * <br>
 * In general, users of the pipeline library has to implement this factory. The library uses then this factory to
 * instantiate {@link PipelineGroupExecutor}s. This allows the user to implement their own {@link PipelineGroupExecutor}s,
 * which generates execution data for pipeline-group execution.
 */
public interface PipelineGroupExecutorFactory {

    /**
     * Creates a {@link PipelineGroupExecutor}
     *
     * @param className Identifier for pipeline classes. Stored at {@link PipelineGroup#getClassName()}. Used from the factory
     *                  to distinguish different types of pipelines.
     * @return
     */
    Optional<PipelineGroupExecutor> fromClassName(String className);

}
