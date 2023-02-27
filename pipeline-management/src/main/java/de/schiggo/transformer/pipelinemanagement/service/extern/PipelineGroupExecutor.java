package de.schiggo.transformer.pipelinemanagement.service.extern;

import java.util.Map;

/**
 * A {@link PipelineGroupExecutor} is an interface to prepare information necessary to execute a group of pipelines.
 * <br>
 * In general, the {@link PipelineGroupExecutor} should be created by a {@link PipelineGroupExecutorFactory}. At
 * instantiation time, the {@link PipelineGroupExecutorFactory} provides the {@link PipelineGroupExecutor} with all
 * dependencies necessary.
 *
 * @see PipelineGroupExecutorFactory
 */
public interface PipelineGroupExecutor {
    /**
     * Generates data necessary to execute a group of pipelines.
     *
     * @param groupAttributes Attributes of the pipeline-group.
     * @return Data necessary for execution of the pipeline-group. It can have any format, the pipelines should just be
     * able to read this format.
     */
    String generateExecutionData(Map<Long, String> groupAttributes);

}
