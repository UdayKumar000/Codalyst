package com.company.codelyst.services;

import com.company.codelyst.exceptions.DatabaseExceptions;
import com.company.codelyst.models.Project;
import com.company.codelyst.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UrlService {
    private final ProjectRepository projectRepository;

    public UrlService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public String getUrlFromProjectId(Long projectId) {
        log.info("Received getUrlFromProjectId request for projectId: {}", projectId);
        Project project = projectRepository.findById(projectId).orElseThrow(
                ()->new DatabaseExceptions("Project not found",null)
        );
        return project.getRepoUrl();
    }
}
