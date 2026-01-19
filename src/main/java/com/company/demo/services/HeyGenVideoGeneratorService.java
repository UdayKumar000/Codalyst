package com.company.demo.services;


import com.company.demo.dto.HeyGenResponse;
import com.company.demo.dto.VideoRequest;
import com.company.demo.exceptions.VideoGenerationException;
import com.company.demo.models.Project;
import com.company.demo.repository.ProjectRepository;
import com.company.demo.utils.CreateVideoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

import java.util.Optional;

@Service
@Slf4j
public class HeyGenVideoGeneratorService {

    private final WebClient webClient;
    private final ProjectRepository projectRepository;

    public HeyGenVideoGeneratorService(@Qualifier("heyGenWebClient") WebClient webClient, ProjectRepository projectRepository) {
        this.webClient = webClient;
        this.projectRepository = projectRepository;
    }


    public String generateVideo(String repoUrl,String scriptFilePath){

        Optional<String> videoId = isUrlExists(repoUrl);

        if(videoId.isPresent()){
            log.info("Repo is already exists");
            return videoId.get();
        }

        if (scriptFilePath == null || scriptFilePath.isBlank()) {
            throw new VideoGenerationException("Script file path must not be empty",null);
        }


        VideoRequest videoRequest;

        try {
            videoRequest = CreateVideoRequest.generateVideoRequest(scriptFilePath);

        } catch (Exception e) {
            throw new VideoGenerationException("Invalid video request data", e);
        }


        try{
            JsonNode res  = webClient.post()
                    .uri("/v2/video/generate")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(videoRequest)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError(),
                            resp -> resp.bodyToMono(String.class)
                                    .map(body -> new VideoGenerationException("Client error form HeyGen API "+body,null))
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            resp -> resp.bodyToMono(String.class)
                                    .map(body -> new VideoGenerationException("Server error form HeyGen API "+body,null))
                    )
                    .bodyToMono(JsonNode.class)
                    .block();

            System.out.println(res.toString());

            HeyGenResponse response = new ObjectMapper().readValue(res.toString(), HeyGenResponse.class);

            if(response == null){
                throw new VideoGenerationException("Server error from HeyGen API",null);
            }

            if(response.getError() != null || response.getData().getVideoId().isEmpty()){
                throw new  VideoGenerationException("Server error from HeyGen API : video_id is empty",null);
            }

            log.info(("Video generation request submitted successfully"));
            return response.getData().getVideoId();

        }catch (VideoGenerationException e){
            log.error("Video generation failed",e);
            throw e;
        }catch (Exception e){
            log.error("Unexpected error during video generation"+e.getMessage());
            throw new VideoGenerationException("Unexpected error while generating video",e);
        }


    }

    private Optional<String> isUrlExists(String repoUrl) {
        return projectRepository.findByRepoUrl(repoUrl).map(Project::getVideoId);
    }


}
