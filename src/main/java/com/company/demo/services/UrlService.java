package com.company.demo.services;

import com.company.demo.exceptions.DatabaseExceptions;
import com.company.demo.models.Project;
import com.company.demo.repository.ProjectRepository;
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
