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

package de.schiggo.transformer.demo.pipeline;

import de.schiggo.transformer.basics.DataSource;
import de.schiggo.transformer.basics.StateContext;
import de.schiggo.transformer.basics.interfaces.Sink;
import de.schiggo.transformer.demo.persistence.source.entity.AddressEntity;
import de.schiggo.transformer.demo.persistence.source.entity.PersonEntity;
import de.schiggo.transformer.demo.persistence.target.entity.PersonReportingEntity;
import de.schiggo.transformer.pipelinemanagement.service.extern.PipelineExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * This pipeline determines all {@link PersonReportingEntity} and store them in the repository.
 * <br>
 * Source entities:
 * <ul>
 *  <li>{@link PersonEntity}</li>
 *  <li>{@link AddressEntity}</li>
 * </ul>
 * <br>
 * Only the first address of a person is considered. Persons without address or with another country code then "DE" gets
 * filtered out.
 *
 * @author Paul Schickling
 */
@RequiredArgsConstructor
@Slf4j
public class SimplePersonPipeline2 implements PipelineExecutor {

    /**
     * Executes the whole pipeline.
     */
    public void execute() {

        // StateContext only necessary for ExceptionHandling
        StateContext<Long> sc = new StateContext<>();

        log.info("Initialize pipeline2");
        Sink<String> sink = new DataSource<String>(Arrays.asList("Eins", "Zwei").iterator()).sink(log::info);
        log.info("Execute Pipeline2");
        sink.execute();
    }

}
