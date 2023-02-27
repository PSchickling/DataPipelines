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

package de.schiggo.transformer.pipelinemanagement.service;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.Pipeline;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelineExecution;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelineGroup;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.Process;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.ExecutionState;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.PipelineRelationType;
import de.schiggo.transformer.pipelinemanagement.persistence.repo.PipelineExecutionRepository;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutorFactory;
import de.schiggo.transformer.pipelinemanagement.wrapper.PipelineExecutorWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Component
@Transactional
public class PipelineExecutionServiceImpl {

    private final PipelineExecutionRepository pipelineExecutionRepository;

    private final PipelineExecutorFactory pipelineExecutorFactory;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void setState(long pipelineExecutionId, ExecutionState executionState) {
        // Load if exists
        var pipelineExecutionOptional = pipelineExecutionRepository.findById(pipelineExecutionId);
        if (pipelineExecutionOptional.isEmpty()) {
            return;
        }
        var pipelineExecution = pipelineExecutionOptional.get();

        // Set
        pipelineExecution.setState(executionState);
        pipelineExecutionRepository.save(pipelineExecution);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Optional<PipelineExecutorWrapper> getExecutionForImmediatelyExecution(Long processId) {
        var pipelineExecutions = pipelineExecutionRepository.findAllByTriggerProcess_IdAndStateIn(processId, ExecutionState.WAITING);
        if (pipelineExecutions.isEmpty()) {
            return Optional.empty();
        }

        for (PipelineExecution pipelineExecution : pipelineExecutions) {
            if (isRunnable(pipelineExecution)) {
                var executorOptional = pipelineExecutorFactory.fromClassName(pipelineExecution.getPipeline().getName(), getAttributes(pipelineExecution.getPipeline()), getAttributes(pipelineExecution.getPipeline().getGroup()), pipelineExecution.getTriggerProcess().getProcessData());
                if (executorOptional.isPresent()) {
                    pipelineExecution.setState(ExecutionState.RUNNING);
                    pipelineExecutionRepository.save(pipelineExecution);
                    return Optional.of(PipelineExecutorWrapper.builder().executor(executorOptional.get()).pipelineExecutionId(pipelineExecution.getId()).build());
                } else {
                    log.debug("No Pipeline-Executor available");
                    // TODO custom exception class
                    throw new RuntimeException("No Pipeline-Executor available");
                }
            }
        }

        // TODO custom exception class
        throw new RuntimeException("No more executable");
    }

    private boolean isRunnable(PipelineExecution pipelineExecution) {
        var requiresFinishPipelines = pipelineExecution.getPipeline().getRelatesTo().stream()
                .filter(rel -> PipelineRelationType.REQUIRES_FINISH.equals(rel.getRelation()))
                .map(rel -> rel.getToPipeline())
                .anyMatch(pipeline -> pipelineExecutionRepository.existsByTriggerProcess_IdAndPipeline_IdAndStateNotIn(pipelineExecution.getTriggerProcess().getId(), pipeline.getId(), ExecutionState.COMPLETED));

        return !requiresFinishPipelines;
    }

    void createExecutions(List<Pipeline> pipelines, Process process) {
        pipelines.stream()
                .map(pipeline -> PipelineExecution.builder().state(ExecutionState.WAITING).pipeline(pipeline).triggerProcess(process).build())
                .forEach(pipelineExecutionRepository::save);
    }

    private Map<Long, String> getAttributes(PipelineGroup group) {
        // Write attributes into a map
        Map<Long, String> groupAttributes = new HashMap<>();
        group.getAttributes().forEach(a -> groupAttributes.put(a.getType().getId(), a.getAttribute()));
        return new HashMap<>();

    }

    private Map<Long, String> getAttributes(Pipeline pipeline) {
        // Write attributes into a map
        Map<Long, String> pipelineAttributes = new HashMap<>();
        pipeline.getAttributes().forEach(a -> pipelineAttributes.put(a.getType().getId(), a.getAttribute()));
        return new HashMap<>();
    }
}
