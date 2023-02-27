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

package de.schiggo.transformer.pipelinemanagement.persistence.repo;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelineExecution;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.ExecutionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PipelineExecutionRepository extends JpaRepository<PipelineExecution, Long> {
    boolean existsByTriggerProcess_IdAndPipeline_IdAndStateNotIn(long processId, long pipelineId, ExecutionState... states);

    List<PipelineExecution> findAllByTriggerProcess_IdAndStateIn(long id, ExecutionState... states);
}