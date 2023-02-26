package de.schiggo.transformer.pipelinemanagement.persistence.entity.mapped;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class NamedEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
