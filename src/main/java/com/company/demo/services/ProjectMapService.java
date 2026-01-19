package com.company.demo.services;

import com.company.demo.exceptions.FileProcessingException;
import com.company.demo.utils.FileFilterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class ProjectMapService {

    private final FileFilterUtils fileFilterUtils = new FileFilterUtils();

    private final CloudDatabaseServices cloudDatabaseServices;
    private final ProjectDatabaseService projectDatabaseService;

    public ProjectMapService(CloudDatabaseServices cloudDatabaseServices, ProjectDatabaseService projectDatabaseService, ProjectDatabaseService projectDatabaseService1) {
        this.cloudDatabaseServices = cloudDatabaseServices;
        this.projectDatabaseService = projectDatabaseService1;
    }

    public void createProjectMap(Long projectId,String projectPath,String projectMapPath){

        File folder = new File(projectPath);

        validateFolder(folder);

        StringBuilder sb = new StringBuilder();

        log.info("Generating project map for {}", projectPath);

        generateTree(folder,"",sb);

        Path destPath = Paths.get(projectMapPath);

        createDirectory(destPath);

        String fileUrl = cloudDatabaseServices.uploadFileToCloud(sb.toString());

        projectDatabaseService.updateMapFileUrl(projectId,fileUrl);

        try(BufferedWriter writer = Files.newBufferedWriter(destPath)){
            writer.write(sb.toString());
            log.info("Project map created at {}", projectMapPath);
        } catch (IOException e) {
            throw new FileProcessingException("Error while writing to project map file ",e);
        }

    }

    private void createDirectory(Path destPath) {
        try{
            Files.createDirectories(destPath.getParent());
        }catch (IOException e){
            throw new FileProcessingException("Unable to create project map file ",e);
        }
    }

    private void validateFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            throw new FileProcessingException("Project directory does not exist: " + folder,null);
        }
    }

    private void generateTree(File folder,String indent, StringBuilder sb){

        File[] files = folder.listFiles();

        if (files != null) {
            for(int i=0;i<files.length;i++){
                File file = files[i];
                if (fileFilterUtils.isExcluded(file.toPath())) continue;

                boolean isLast = (i==files.length-1);
                sb.append(indent).append(isLast ? "└── " : "├── ").append(file.getName()).append("\n");

                if (file.isDirectory()) {
                    generateTree(file, indent + (isLast ? "    " : "│   "), sb);
                }

            }
        }

    }




}
