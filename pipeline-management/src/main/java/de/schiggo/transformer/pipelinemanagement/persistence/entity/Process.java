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

package de.schiggo.transformer.pipelinemanagement.persistence.entity;


import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.ExecutionState;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * Entity representing a planned or performed execution of a {@link PipelineGroup}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_process")
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "processid")
    private Long id;

    @Column(name = "executionstateid")
    private ExecutionState state;

    @Column(name = "processdata")
    private String processData;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipelinegroupid")
    private PipelineGroup group;

    @ToString.Exclude
    @OneToMany(mappedBy = "triggerProcess", fetch = FetchType.LAZY)
    List<PipelineExecution> pipelineExecutions;

}
