package de.schiggo.transformer.pipelinemanagement.persistence.repo;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelineGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PipelineGroupRepository extends JpaRepository<de.schiggo.transformer.pipelinemanagement.persistence.entity.PipelineGroup, Long> {
    Optional<PipelineGroup> findByName(@NonNull String name);
}