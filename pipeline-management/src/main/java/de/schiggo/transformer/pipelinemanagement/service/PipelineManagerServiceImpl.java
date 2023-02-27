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
