package com.company.demo.services;

import com.company.demo.dto.VideoClientResponse;
import com.company.demo.exceptions.VideoClientException;
import com.company.demo.models.Project;
import com.company.demo.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;

@Service
@Slf4j
public class VideoClient {


    private final WebClient webClient;
    private final ProjectRepository projectRepository;

    public VideoClient(WebClient webClient, ProjectRepository projectRepository) {
        this.webClient = webClient;
        this.projectRepository = projectRepository;
    }

    public VideoClientResponse retrieveVideoFromProjectId(Long projectId){
        log.info("Retrieving video from project id {}", projectId);
        try {
            Project project = projectRepository.findById(projectId).orElseThrow(
                    () -> new VideoClientException("Project not found", null)
            );


            return retrieveVideoFromVideoId(project.getVideoId());

        }catch (Exception e){
            log.error("Error while retrieving video from projectId {}",projectId,e);
            throw new VideoClientException("Unknown exception found during video retrieval ", null);
        }


    }

    private VideoClientResponse retrieveVideoFromVideoId(String videoId){
        try{

            if(videoId == null){
                throw new VideoClientException("Video Id not found",null);
            }

            JsonNode json = webClient.get().uri(uriBuilder ->
                    uriBuilder
                            .path("/v1/video_status.get")
                            .queryParam("video_id",videoId)
                            .build()
            ).retrieve().bodyToMono(JsonNode.class).block();

            ObjectMapper mapper = new ObjectMapper();

            if(json == null){
                throw new VideoClientException("Video not found", null);
            }

            log.info("Video response: {}", json);
            return mapper.readValue(json.toString(), VideoClientResponse.class);

        } catch (WebClientResponseException e) {
            log.error("Failed to retrieve video: {} | Status: {}", videoId, e.getStatusCode());
            throw new VideoClientException("Video API returned error", e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving video: {}", videoId, e);
            throw new VideoClientException("Unexpected error while calling video API", e);
        }

    }
}
