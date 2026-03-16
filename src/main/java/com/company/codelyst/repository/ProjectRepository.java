package com.company.codelyst.repository;

import com.company.codelyst.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    Optional<Project> findByRepoUrl(String repoUrl);
}
