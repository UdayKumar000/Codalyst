package com.company.demo.services;

import com.company.demo.exceptions.VideoGenerationException;
import com.company.demo.proccessor.VideoProccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GenerateVideo {

    private final CodePackService codePackService;
    private final ScriptService scriptService;
    private final ProjectMapService projectMapService;
    private final VideoClient videoClient;
    private final GitService gitService;
    private final HeyGenVideoGeneratorService heyGenVideoGeneratorService;

    public GenerateVideo(CodePackService codePackService,
                         ScriptService scriptService,
                         ProjectMapService projectMapService,
                         VideoClient videoClient,
                         GitService gitService,
                         HeyGenVideoGeneratorService heyGenVideoGeneratorService){
        this.codePackService = codePackService;
        this.scriptService = scriptService;
        this.projectMapService = projectMapService;
        this.videoClient = videoClient;
        this.gitService = gitService;
        this.heyGenVideoGeneratorService = heyGenVideoGeneratorService;
    }

    public void generateVideo(String url){

        VideoProccessor videoProccessor = new VideoProccessor(url);


        try{

            //clone url
            gitService.cloneRepository(videoProccessor.getRepoUrl(),videoProccessor.getRepoPath());

            //generate project map
            projectMapService.createProjectMap(videoProccessor.getRepoPath(),videoProccessor.getProjectMapPath());

            //pack the code
            codePackService.packCode(videoProccessor.getRepoPath(),videoProccessor.getXmlFilePath());

            //generate script
            scriptService.generateScript(videoProccessor.getXmlFilePath(),videoProccessor.getScriptFilePath());

            //generate video
//            heyGenVideoGeneratorService.generateVideo(videoProccessor.getScriptFilePath());

            //retrieve video
//            videoClient.retrieveVideo("");

        }
        catch (RuntimeException ex) {
            log.error("Video Generation Failed : {}",ex.getMessage());

//            throw new VideoGenerationException(
//                    "Video generation failed for repo " + videoProccessor.getRepoUrl(), ex
//            );
        } catch (Exception e) {

            log.error("Video Generation Failed : {}",e.getMessage());
//            throw new RuntimeException("Large Exception"+e);
        }


    }




}
