package de.schiggo.transformer.pipelinemanagement.persistence.repo;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelineExecution;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.ExecutionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PipelineExecutionRepository extends JpaRepository<PipelineExecution, Long> {
    boolean existsByTriggerProcess_IdAndPipeline_IdAndStateNotIn(long processId, long pipelineId, ExecutionState... states);

    List<PipelineExecution> findAllByTriggerProcess_IdAndStateIn(long id, ExecutionState... states);
}