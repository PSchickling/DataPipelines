package de.schiggo.transformer.pipelinemanagement.persistence.repo;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.Process;
import de.schiggo.transformer.pipelinemanagement.persistence.entity.constant.ExecutionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessRepository extends JpaRepository<Process, Long> {
    List<Process> findAllByStateInOrderByIdAsc(ExecutionState... states);
}