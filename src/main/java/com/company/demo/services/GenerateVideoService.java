package com.company.demo.services;

import com.company.demo.dto.VideoResponse;
import com.company.demo.models.Project;
import com.company.demo.proccessor.VideoProccessor;
import com.company.demo.utils.DirectoryDeleter;
import com.company.demo.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class GenerateVideoService {

    private final CodePackService codePackService;
    private final ScriptService scriptService;
    private final ProjectMapService projectMapService;
    private final VideoClient videoClient;
    private final GitService gitService;
    private final HeyGenVideoGeneratorService heyGenVideoGeneratorService;
    private final ProjectDatabaseService projectDatabaseService;

    public GenerateVideoService(CodePackService codePackService,
                                ScriptService scriptService,
                                ProjectMapService projectMapService,
                                VideoClient videoClient,
                                GitService gitService,
                                HeyGenVideoGeneratorService heyGenVideoGeneratorService,
                                ProjectDatabaseService projectDatabaseService) {
        this.codePackService = codePackService;
        this.scriptService = scriptService;
        this.projectMapService = projectMapService;
        this.videoClient = videoClient;
        this.gitService = gitService;
        this.heyGenVideoGeneratorService = heyGenVideoGeneratorService;
        this.projectDatabaseService = projectDatabaseService;
    }

    public Response<VideoResponse> generateVideo(String url){

        VideoProccessor videoProccessor = new VideoProccessor(url);


        try{

            //clone url
            gitService.cloneRepository(videoProccessor.getRepoUrl(),videoProccessor.getTempRepoPath());

            // project already exists return it if not create
            Project project = projectDatabaseService.registerToDatabase(videoProccessor.getRepoUrl());

            if(project.getVideoId()!=null){
                log.info("Project has been registered with valid video id already");
                return new Response<>(true, List.of(new VideoResponse(project.getId(),project.getVideoId())),"Video Generation Started");
            }

            //generate project map
            projectMapService.createProjectMap(project.getId(),videoProccessor.getTempRepoPath(),videoProccessor.getProjectMapPath());

            //pack the code
            codePackService.packCode(project.getId(),videoProccessor.getTempRepoPath(),videoProccessor.getXmlFilePath());

            //generate script
            scriptService.generateScript(project.getId(),videoProccessor.getXmlFilePath(),videoProccessor.getScriptFilePath());

            //generate video
            String videoId = heyGenVideoGeneratorService.generateVideo(videoProccessor.getRepoUrl(),videoProccessor.getScriptFilePath());

            // update video id
            projectDatabaseService.updateVideoId(project.getId(),videoId);


            //return the videoId and project id
            return new Response<>(true, List.of(new VideoResponse(project.getId(),videoId)),"Video Generation Started");

        }
        catch (RuntimeException ex) {
            log.error("Video Generation Failed : {}",ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error("Unknown Video Generation Exception : {}",e.getMessage());
            throw e;
        }
        finally{
            try {
                DirectoryDeleter.deleteDirectory(Paths.get(videoProccessor.getTempRepoPath()));
            } catch (Exception cleanupEx) {
                log.error("Failed to cleanup temp repo directory: {}",
                        videoProccessor.getTempRepoPath(), cleanupEx);
            }
        }


    }




}
