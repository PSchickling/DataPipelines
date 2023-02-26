package de.schiggo.transformer.pipelinemanagement.persistence.entity;


import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.ExecutionState;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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
