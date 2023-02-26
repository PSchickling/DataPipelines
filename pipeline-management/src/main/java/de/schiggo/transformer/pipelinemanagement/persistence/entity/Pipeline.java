package de.schiggo.transformer.pipelinemanagement.persistence.entity;


import de.schiggo.transformer.pipelinemanagement.persistence.entity.mapped.NamedEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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
