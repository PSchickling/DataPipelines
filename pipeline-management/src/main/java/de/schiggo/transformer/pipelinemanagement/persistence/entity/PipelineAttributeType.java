package de.schiggo.transformer.pipelinemanagement.persistence.entity;


import de.schiggo.transformer.pipelinemanagement.persistence.entity.mapped.NamedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_pipelineattributetype")
public class PipelineAttributeType extends NamedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipelineattributetypeid")
    private Long id;

    @Column(name = "classname")
    private String className;

}
