package de.schiggo.transformer.pipelinemanagement.service;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.Pipeline;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelineAttribute;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelineAttributeType;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelinePipeline;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.PipelineRelationType;
import de.schiggo.transformer.pipelinemanagement.persistence.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@Component
@RequiredArgsConstructor
public class PipelineServiceImpl {

    private final PipelineRepository pipelineRepository;

    private final PipelineGroupRepository pipelineGroupRepository;

    private final PipelineAttributeRepository pipelineAttributeRepository;
    private final PipelineAttributeTypeRepository pipelineAttributeTypeRepository;
    private final PipelinePipelineRepository pipelinePipelineRepository;

    public Optional<Long> createPipeline(String name, String description, String className, Map<Long, String> attributes, String groupName) {
        var group = pipelineGroupRepository.findByName(groupName);
        if (group.isEmpty()) {
            return Optional.empty();
        }

        var pipeline = new Pipeline();
        pipeline.setName(name);
        pipeline.setDescription(description);
        pipeline.setClassName(className);
        pipeline.setGroup(group.get());
        pipelineRepository.save(pipeline);

        List<PipelineAttribute> pipelineAttributes = new ArrayList<>();
        for (Long key : attributes.keySet()) {
            var attrType = pipelineAttributeTypeRepository.findById(key);
            if (attrType.isPresent()) {
                var newAttribute = PipelineAttribute.builder().type(attrType.get()).attribute(attributes.get(key)).pipeline(pipeline).build();
                pipelineAttributeRepository.save(newAttribute);
            } else {
                // TODO Error or ignore?
            }
        }

        return Optional.of(pipeline.getId());
    }

    public void addPipelineAttributeType(long id, String name, String description) {
        if (pipelineAttributeTypeRepository.existsById(id)) {
            // TODO ignore or error?
            return;
        }

        var type = new PipelineAttributeType();
        type.setId(id);
        type.setName(name);
        type.setDescription(description);
        pipelineAttributeTypeRepository.save(type);
    }

    public Map<Long, String> getAttributes(long pipelineId) {
        // Load pipeline
        var pipelineOptional = pipelineRepository.findById(pipelineId);
        if (pipelineOptional.isEmpty()) {
            return new HashMap<>();
        }
        var pipeline = pipelineOptional.get();

        // Write attributes into a map
        Map<Long, String> pipelineAttributes = new HashMap<>();
        pipeline.getAttributes().forEach(a -> pipelineAttributes.put(a.getType().getId(), a.getAttribute()));
        return new HashMap<>();
    }

    public void requiresFinish(String pipelineNameRequires, String pipelineNameIsRequired) {
        var from = pipelineRepository.findByName(pipelineNameRequires);
        var to = pipelineRepository.findByName(pipelineNameIsRequired);
        if (from.isEmpty() || to.isEmpty()) {
            return;
        }
        if (from.get().getId().equals(to.get().getId())) {
            return;
        }

        var relation = PipelinePipeline.builder().fromPipeline(from.get()).toPipeline(to.get()).relation(PipelineRelationType.REQUIRES_FINISH).build();
        pipelinePipelineRepository.save(relation);
    }

}
