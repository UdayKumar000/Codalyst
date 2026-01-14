package com.company.demo.services;


import com.company.demo.dto.VideoRequest;
import com.company.demo.exceptions.VideoGenerationException;
import com.company.demo.utils.CreateVideoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

@Service
@Slf4j
public class HeyGenVideoGeneratorService {

    private final WebClient webClient;

    public HeyGenVideoGeneratorService(@Qualifier("heyGenWebClient") WebClient webClient) {
        this.webClient = webClient;
    }


    public JsonNode generateVideo(String scriptFilePath){

        if (scriptFilePath == null || scriptFilePath.isBlank()) {
            throw new VideoGenerationException("Script file path must not be empty");
        }


        VideoRequest videoRequest;

        try {
            videoRequest = CreateVideoRequest.generateVideoRequest(scriptFilePath);

        } catch (Exception e) {
            throw new VideoGenerationException("Invalid video request data", e);
        }


        try{
            JsonNode response  = webClient.post()
                    .uri("/v2/video/generate")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(videoRequest)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError(),
                            resp -> resp.bodyToMono(String.class)
                                    .map(body -> new VideoGenerationException("Client error form HeyGen API "+body))
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            resp -> resp.bodyToMono(String.class)
                                    .map(body -> new VideoGenerationException("Server error form HeyGen API "+body))
                    )
                    .bodyToMono(JsonNode.class)
                    .block();

            if(response == null){
                throw new VideoGenerationException("Server error from HeyGen API");
            }

            log.info(("Video generation request submitted successfully"));

            return response;

        }catch (VideoGenerationException e){
            log.error("Video generation failed",e);
            throw e;
        }catch (Exception e){
            log.error("Unexpected error during video generation",e);
            throw new VideoGenerationException("Unexpected error while generating video",e);
        }


    }


}
