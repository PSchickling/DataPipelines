package de.schiggo.transformer.pipelinemanagement.wrapper;

import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PipelineExecutorWrapper {

    private PipelineExecutor executor;

    private Long pipelineExecutionId;
}
