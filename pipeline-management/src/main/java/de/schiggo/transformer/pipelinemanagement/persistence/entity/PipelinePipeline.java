package de.schiggo.transformer.pipelinemanagement.persistence.entity;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.PipelineRelationType;
import lombok.*;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "t_pipelinepipeline")
public class PipelinePipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipelinepipelineid")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frompipelineid")
    private Pipeline fromPipeline;

    @Column(name = "pipelinepipelinetypeid")
    private PipelineRelationType relation;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topipelineid")
    private Pipeline toPipeline;
}
