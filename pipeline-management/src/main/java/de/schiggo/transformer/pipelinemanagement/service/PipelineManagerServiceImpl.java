package de.schiggo.transformer.pipelinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Component
@RequiredArgsConstructor
public class PipelineManagerServiceImpl {

    private final PipelineGroupServiceImpl pipelineGroupService;
    private final PipelineServiceImpl pipelineService;

    public void triggerGroup(String groupName) {
        pipelineGroupService.triggerGroup(groupName);
    }

    public void addPipelineAttributeType(long id, String name, String description) {
        pipelineService.addPipelineAttributeType(id, name, description);
    }

    public void addPipeline(String name, String description, String className, Map<Long, String> attributes, String groupName) {
        pipelineService.createPipeline(name, description, className, attributes, groupName);
    }

    public void addPipelineGroupAttributeTypes(long id, String name, String description) {
        pipelineGroupService.addPipelineGroupAttributeType(id, name, description);
    }

    public void addPipelineGroup(String name, String description, String className, Map<Long, String> attributes) {
        pipelineGroupService.createPipelineGroup(name, description, className, attributes);
    }

    public void executePipeline() {
        pipelineGroupService.executePipeline();
    }

    public void requiresFinish(String pipelineNameRequires, String pipelineNameIsRequired) {
        pipelineService.requiresFinish(pipelineNameRequires, pipelineNameIsRequired);
    }
}
