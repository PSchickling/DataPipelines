package de.schiggo.transformer.pipelinemanagement.persistence.entity;


import lombok.*;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "t_pipelinegroupattribute")
public class PipelineGroupAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipelinegroupattributeid")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipelinegroupid")
    private PipelineGroup group;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipelinegroupattributetypeid")
    private PipelineGroupAttributeType type;

    @Column(name = "attribute")
    private String attribute;

}
