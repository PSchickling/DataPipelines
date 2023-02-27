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

package de.schiggo.transformer.pipelinemanagement.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Properties-Class for the pipeline management library.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PipelineManagementProperties {

    /**
     * If true, then pipeline management will be enabled.
     */
    private boolean enabled = false;

    /**
     * If true, then liquibase will be executed and initialize the tables for pipeline management. If false nothing will happen.
     */
    private boolean liquibaseEnabled = false;

    /**
     * Hibernate dialect for the pipeline-management datasource.
     */
    private String hibernateDialect;


}
