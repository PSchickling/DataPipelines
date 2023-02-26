package de.schiggo.transformer.pipelinemanagement.service;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.Process;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.*;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.ExecutionState;
import de.schiggo.transformer.pipelinemanagement.persistence.repo.PipelineGroupAttributeRepository;
import de.schiggo.transformer.pipelinemanagement.persistence.repo.PipelineGroupAttributeTypeRepository;
import de.schiggo.transformer.pipelinemanagement.persistence.repo.PipelineGroupRepository;
import de.schiggo.transformer.pipelinemanagement.persistence.repo.ProcessRepository;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineGroupExecutorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Component
@Transactional
@RequiredArgsConstructor
public class PipelineGroupServiceImpl {

    private final ProcessRepository processRepository;
    private final PipelineGroupAttributeTypeRepository pipelineGroupAttributeTypeRepository;
    private final PipelineGroupAttributeRepository pipelineGroupAttributeRepository;
    private final PipelineGroupRepository pipelineGroupRepository;

    private final PipelineExecutionServiceImpl pipelineExecutionService;

    private final PipelineGroupExecutorFactory pipelineGroupExecutorFactory;

    public void triggerGroup(String groupName) {
        // Load group
        var groupOptional = pipelineGroupRepository.findByName(groupName);
        if (groupOptional.isEmpty()) {
            return;
        }
        var group = groupOptional.get();

        // load group executor
        var groupExecutorOptional = pipelineGroupExecutorFactory.fromName(groupName);
        if (groupExecutorOptional.isEmpty()) {
            // TODO throw error
            return;
        }
        var groupExecutor = groupExecutorOptional.get();

        // TODO check if group can be executed (e.g. is group already executing)?

        // Generate process data
        String processData = groupExecutor.generateExecutionData(getAttributes(group));

        // Initialize process
        Process process = processRepository.save(Process.builder().group(group).state(ExecutionState.WAITING).processData(processData).build());

        // Initialize pipeline executions
        pipelineExecutionService.createExecutions(group.getPipelines(), process);
    }

    public void executePipeline() {
        // TODO find all and iterate, because maybe no executor for this one
        var processes = processRepository.findAllByStateInOrderByIdAsc(ExecutionState.WAITING, ExecutionState.RUNNING);
        if (processes.isEmpty()) {
            return;
        }

        for (Process process : processes) {
            var pipelineExecutor = pipelineExecutionService.getExecutionForImmediatelyExecution(process.getId());
            if (pipelineExecutor.isPresent()) {
                try {
                    pipelineExecutor.get().getExecutor().execute();
                    pipelineExecutionService.setState(pipelineExecutor.get().getPipelineExecutionId(), ExecutionState.COMPLETED);
                } catch (Exception e) {
                    pipelineExecutionService.setState(pipelineExecutor.get().getPipelineExecutionId(), ExecutionState.FAILED);
                }
                checkFinishedProcesses();
                return;
            }
        }
    }

    private void checkFinishedProcesses() {
        // TODO Check if process is finished or stopped because of failures
    }

    public void addPipelineGroupAttributeType(long id, String name, String description) {
        if (pipelineGroupAttributeTypeRepository.existsById(id)) {
            // TODO ignore or error?
            return;
        }

        var type = new PipelineGroupAttributeType();
        type.setId(id);
        type.setName(name);
        type.setDescription(description);
        pipelineGroupAttributeTypeRepository.save(type);
    }

    public Optional<Long> createPipelineGroup(String name, String description, String className, Map<Long, String> attributes) {
        var group = new PipelineGroup();
        group.setName(name);
        group.setDescription(description);
        group.setClassName(className);
        pipelineGroupRepository.save(group);

        List<PipelineAttribute> pipelineAttributes = new ArrayList<>();
        for (Long key : attributes.keySet()) {
            var attrType = pipelineGroupAttributeTypeRepository.findById(key);
            if (attrType.isPresent()) {
                var newAttribute = PipelineGroupAttribute.builder().type(attrType.get()).attribute(attributes.get(key)).group(group).build();
                pipelineGroupAttributeRepository.save(newAttribute);
            } else {
                // TODO Error or ignore?
            }
        }

        return Optional.of(group.getId());
    }

    private Map<Long, String> getAttributes(PipelineGroup group) {
        // Write attributes into a map
        Map<Long, String> groupAttributes = new HashMap<>();
        group.getAttributes().forEach(a -> groupAttributes.put(a.getType().getId(), a.getAttribute()));
        return new HashMap<>();

    }
}
