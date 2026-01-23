package com.company.demo.services;

import com.company.demo.dto.RadarResponse;
import com.company.demo.exceptions.DatabaseExceptions;
import com.company.demo.models.Project;
import com.company.demo.models.ProjectRadarInfo;
import com.company.demo.repository.ProjectRadarInfoRepository;
import com.company.demo.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RadarDatabaseService {
    private final ProjectRadarInfoRepository projectRadarInfoRepository;
    private final ProjectRepository projectRepository;

    public RadarDatabaseService(ProjectRadarInfoRepository projectRadarInfoRepository, ProjectRepository projectRepository) {
        this.projectRadarInfoRepository = projectRadarInfoRepository;
        this.projectRepository = projectRepository;
    }

    public void updateToDatabase(Long projectId, RadarResponse radarResponse) {
        Project project =  projectRepository.findById(projectId).orElseThrow(() -> new DatabaseExceptions("Project not found",null));
        ProjectRadarInfo projectRadarInfo = new ProjectRadarInfo();
        List<Integer> data = radarResponse.getData();

        if(data.size()<6){
            throw new DatabaseExceptions("Radar database error invalid input",null);
        }

        projectRadarInfo.setProject(project);

        projectRadarInfo.setCodeQuality(data.get(0));
        projectRadarInfo.setArchitecture(data.get(1));
        projectRadarInfo.setReliability(data.get(2));
        projectRadarInfo.setPerformance(data.get(3));
        projectRadarInfo.setSecurity(data.get(4));
        projectRadarInfo.setTestability(data.get(5));

        projectRadarInfoRepository.save(projectRadarInfo);

    }

}
