package com.company.demo.services;

import com.company.demo.exceptions.DatabaseExceptions;
import com.company.demo.models.Project;
import com.company.demo.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class ProjectDatabaseService {
    ProjectRepository projectRepository;
    public ProjectDatabaseService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project registerToDatabase(String repoUrl){

        try{
            Optional<Project> project = projectRepository.findByRepoUrl(repoUrl);
            if(project.isPresent()){
                return project.get();
            }

            Project newProject =  new Project(repoUrl);
            return projectRepository.save(newProject);

        }catch(DatabaseExceptions e){
            log.error("Error in inserting data to database: ",e);
            throw e;
        }catch(Exception e){
            log.error("Unknown Exception occurred while inserting into database",e);
            throw e;
        }


    }



    @Transactional
    public void updateVideoId(Long id, String videoId) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new DatabaseExceptions("Project not found ",null));
        project.setVideoId(videoId);
        project.setVideoStatus("PROCESSING");
    }

    @Transactional
    public void updateMapFileUrl(Long id,String mapFileUrl) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new DatabaseExceptions("Project not found ",null));
        project.setMapFileUrl(mapFileUrl);
    }

    @Transactional
    public void updateXmlFileUrl(Long id,String xmlFileUrl){
        Project project = projectRepository.findById(id).orElseThrow(() -> new DatabaseExceptions("Project not found ",null));
        project.setXmlFileUrl(xmlFileUrl);
    }
}
