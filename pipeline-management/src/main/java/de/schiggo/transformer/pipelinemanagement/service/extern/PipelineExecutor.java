package de.schiggo.transformer.pipelinemanagement.service.extern;

/**
 * A {@link PipelineExecutor} is an interface to execute a pipeline.
 * <br>
 * In general, the {@link PipelineExecutor} should be created by a {@link PipelineExecutorFactory}. At instantiation, the
 * {@link PipelineExecutorFactory} provides the {@link PipelineExecutor} with all dependencies necessary.
 *
 * @see PipelineExecutorFactory
 */
public interface PipelineExecutor {

    /**
     * Executes the pipeline.
     */
    void execute();

}
