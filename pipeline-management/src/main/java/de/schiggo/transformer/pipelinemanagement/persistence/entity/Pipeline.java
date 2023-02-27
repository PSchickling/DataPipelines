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


import de.schiggo.transformer.pipelinemanagement.persistence.entity.mapped.NamedEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * Entity for Pipelines.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_pipeline")
public class Pipeline extends NamedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipelineid")
    private Long id;

    @Column(name = "classname")
    private String className;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipelinegroupid")
    private PipelineGroup group;

    @ToString.Exclude
    @OneToMany(mappedBy = "pipeline", fetch = FetchType.LAZY)
    List<PipelineAttribute> attributes;

    @ToString.Exclude
    @OneToMany(mappedBy = "fromPipeline", fetch = FetchType.LAZY)
    List<PipelinePipeline> relatesTo;

    @ToString.Exclude
    @OneToMany(mappedBy = "toPipeline", fetch = FetchType.LAZY)
    List<PipelinePipeline> relatedFrom;

}
