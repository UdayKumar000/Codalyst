package com.company.demo.services;

import com.company.demo.exceptions.VideoClientException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
public class VideoClient {


    private final WebClient webClient;

    public VideoClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public JsonNode retrieveVideo(String videoId){
        try{
            JsonNode json = webClient.get().uri(uriBuilder ->
                    uriBuilder
                            .path("/v1/video_status.get")
                            .queryParam("video_id","f43bbcba867f4842b50607471894e464")
                            .build()
            ).retrieve().bodyToMono(JsonNode.class).block();
            System.out.println(json);


            log.info("Video response: {}", json);
            return json;
        } catch (WebClientResponseException e) {
            log.error("Failed to retrieve video: {} | Status: {}", videoId, e.getStatusCode());
            throw new VideoClientException("Video API returned error", e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving video: {}", videoId, e);
            throw new VideoClientException("Unexpected error while calling video API", e);
        }

    }
}
