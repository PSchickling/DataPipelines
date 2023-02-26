package de.schiggo.transformer.pipelinemanagement.persistence.entity;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.ExecutionState;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_pipelineexecution")
public class PipelineExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pipelineexecutionid")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipelineid")
    private Pipeline pipeline;

    @Column(name = "executionstateid")
    private ExecutionState state;

    @Column(name = "start")
    private LocalDateTime start;

    @Column(name = "finish")
    private LocalDateTime finish;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggerprocessid")
    private Process triggerProcess;

}
