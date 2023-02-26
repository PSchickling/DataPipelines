package de.schiggo.transformer.pipelinemanagement.persistence.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExecutionState {

    WAITING(1, ""),
    RUNNING(2, ""),
    PAUSED(3, ""),
    COMPLETED(4, ""),
    FAILED(5, "");

    private final long id;
    private final String name;

}
