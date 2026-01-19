package com.company.demo.services;

import com.company.demo.exceptions.DatabaseExceptions;
import com.company.demo.models.Project;
import com.company.demo.models.ProjectQuadrantInfo;
import com.company.demo.repository.ProjectQuadrantInfoRepository;
import com.company.demo.repository.ProjectRepository;
import com.company.demo.utils.DataValue;
import com.company.demo.utils.GeminiJsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuadrantInfoDatabaseService {
    private final ProjectQuadrantInfoRepository projectQuadrantInfoRepository;
    private final ProjectRepository projectRepository;

    public QuadrantInfoDatabaseService(ProjectQuadrantInfoRepository projectQuadrantInfoRepository, ProjectRepository projectRepository) {
        this.projectQuadrantInfoRepository = projectQuadrantInfoRepository;
        this.projectRepository = projectRepository;
    }

    public void updateScoresToDatabase(Long projectId,GeminiJsonResponse response) {

        List<DataValue> dataValues = response.getData();

        try{

            Project project = projectRepository.findById(projectId).orElseThrow(
                    () -> new DatabaseExceptions("Project not found",null)
            );

            for (DataValue dataValue : dataValues) {

                ProjectQuadrantInfo projectQuadrantInfo =
                        projectQuadrantInfoRepository
                                .findByProjectIdAndFileName(projectId, dataValue.getFileName())
                                .orElseGet(ProjectQuadrantInfo::new);
                projectQuadrantInfo.setProject(project);
                projectQuadrantInfo.setXScore(dataValue.getXScore());
                projectQuadrantInfo.setYScore(dataValue.getYScore());
                projectQuadrantInfo.setFileName(dataValue.getFileName());
                projectQuadrantInfo.setFileLayer(dataValue.getFileLayer());
                projectQuadrantInfoRepository.save(projectQuadrantInfo);
            }
        }catch(Exception e){
            log.error(("Unknown error while updating quadrant scores to database"),e);
            throw e;
        }

    }
}
