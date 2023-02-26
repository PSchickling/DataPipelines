package de.schiggo.transformer.pipelinemanagement.persistence.entity;


import lombok.*;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "t_pipelineattribute")
public class PipelineAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipelineattributeid")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipelineid")
    private Pipeline pipeline;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipelineattributetypeid")
    private PipelineAttributeType type;

    @Column(name = "attribute")
    private String attribute;

}
