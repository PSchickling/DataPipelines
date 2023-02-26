package de.schiggo.transformer.pipelinemanagement.persistence.entity;


import de.schiggo.transformer.pipelinemanagement.persistence.entity.mapped.NamedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_pipelinegroup")
public class PipelineGroup extends NamedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipelinegroupid")
    private Long id;

    @Column(name = "classname")
    private String className;

    @ToString.Exclude
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    List<Pipeline> pipelines;

    @ToString.Exclude
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    List<PipelineGroupAttribute> attributes;

}
