/*
 * Copyright 2023 Paul Schickling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.schiggo.transformer.pipelinemanagement.persistence.entity.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

/**
 * Converts between {@link ExecutionState} and {@link Long}. Used in JPA to map ids to the execution-states, which are
 * represented as enums instead of entities.
 */
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
