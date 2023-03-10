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

package de.schiggo.transformer.app.controller;

import de.schiggo.transformer.app.persistence.target.enity.PersonReportingEntity;
import de.schiggo.transformer.app.persistence.target.repo.PersonReportingRepository;
import de.schiggo.transformer.app.pipeline.SimplePersonPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    final SimplePersonPipeline personPipeline;

    final PersonReportingRepository personReportingRepository;

    @GetMapping
    public Iterable<PersonReportingEntity> test() {
        Calendar now = Calendar.getInstance();
        personPipeline.execute(now);
        return personReportingRepository.findAll();
    }

}
