package de.schiggo.transformer.pipelinemanagement.persistence.entity;


import de.schiggo.transformer.pipelinemanagement.persistence.entity.mapped.NamedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_pipelinegroupattributetype")
public class PipelineGroupAttributeType extends NamedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipelinegroupattributetypeid")
    private Long id;

    @Column(name = "classname")
    private String className;

}
