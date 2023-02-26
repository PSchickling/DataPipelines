package de.schiggo.transformer.pipelinemanagement.persistence.entity.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class PipelineRelationTypeConverter implements AttributeConverter<PipelineRelationType, Long> {
    @Override
    public Long convertToDatabaseColumn(PipelineRelationType pipelineRelationType) {
        if (pipelineRelationType == null) {
            return null;
        }

        return pipelineRelationType.getId();
    }

    @Override
    public PipelineRelationType convertToEntityAttribute(Long pipelineRelationTypeId) {
        if (pipelineRelationTypeId == null) {
            return null;
        }

        return Stream.of(PipelineRelationType.values()).filter(v -> v.getId() == pipelineRelationTypeId.longValue()).findFirst().orElse(null);
    }
}
