package com.company.demo.repository;

import com.company.demo.models.ProjectC4Diagrams;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectC4DiagramsRepository extends JpaRepository<ProjectC4Diagrams, Long> {
    Optional<ProjectC4Diagrams> findByProjectId(Long projectId);
}
