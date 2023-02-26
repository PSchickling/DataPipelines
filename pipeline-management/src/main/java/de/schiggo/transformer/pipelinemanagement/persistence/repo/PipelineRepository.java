package de.schiggo.transformer.pipelinemanagement.persistence.repo;

import de.schiggo.transformer.pipelinemanagement.persistence.entity.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PipelineRepository extends JpaRepository<Pipeline, Long> {
    Optional<Pipeline> findByName(@NonNull String name);
}