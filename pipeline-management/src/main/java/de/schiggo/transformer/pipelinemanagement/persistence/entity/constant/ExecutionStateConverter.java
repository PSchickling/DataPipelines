package de.schiggo.transformer.pipelinemanagement.persistence.entity.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ExecutionStateConverter implements AttributeConverter<ExecutionState, Long> {
    @Override
    public Long convertToDatabaseColumn(ExecutionState executionState) {
        if (executionState == null) {
            return null;
        }

        return executionState.getId();
    }

    @Override
    public ExecutionState convertToEntityAttribute(Long executionStateId) {
        if (executionStateId == null) {
            return null;
        }

        return Stream.of(ExecutionState.values()).filter(v -> v.getId() == executionStateId.longValue()).findFirst().orElse(null);
    }
}
