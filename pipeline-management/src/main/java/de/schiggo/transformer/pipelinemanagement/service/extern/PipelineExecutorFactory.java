package de.schiggo.transformer.pipelinemanagement.service.extern;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.Pipeline;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link PipelineExecutorFactory} is a factory to instantiate {@link PipelineExecutor}s.
 * <br>
 * In general, users of the pipeline library has to implement this factory. The library uses then this factory to
 * instantiate {@link PipelineExecutor}s. This allows the user to define it own implementation of pipelines.
 */
public interface PipelineExecutorFactory {

    /**
     * Creates a {@link PipelineExecutor}.
     *
     * @param className               Identifier for pipeline classes. Stored at {@link Pipeline#getClassName()}. Used from the factory
     *                                to distinguish different types of pipelines.
     * @param pipelineAttributes      Attributes of the pipeline
     * @param pipelineGroupAttributes Attributes of the pipeline group
     * @param processData             data of the execution process
     * @return The generated pipeline executor
     */
    Optional<PipelineExecutor> fromClassName(String className, Map<Long, String> pipelineAttributes, Map<Long, String> pipelineGroupAttributes, String processData);

}
