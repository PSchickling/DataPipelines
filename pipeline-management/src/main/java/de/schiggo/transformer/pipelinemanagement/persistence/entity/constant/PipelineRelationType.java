package de.schiggo.transformer.pipelinemanagement.persistence.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PipelineRelationType {

    REQUIRES_FINISH(1, "From-Pipeline can only start if To-Pipeline finished");

    private final long id;
    private final String name;

}
