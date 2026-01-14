package com.company.demo.gemini;


import com.company.demo.dto.VideoRequest;
import com.company.demo.utils.CreateVideoRequest;
import com.company.demo.webclient.ConfigWebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;


public class HeyGenVideoGenerator {



    private static WebClient webClient = ConfigWebClient.webClient();

    public static void generateVideo(String scriptFilePath){

        VideoRequest videoRequest = CreateVideoRequest.generateVideoRequest(scriptFilePath);

        try {
            System.out.println(new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(videoRequest));
        } catch (JsonProcessingException e) {
            System.err.println(e.getMessage());
        }

    try{
        JsonNode response  = webClient.post().uri("/v2/video/generate")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(videoRequest)
                .retrieve().bodyToMono(JsonNode.class).block();
        System.out.println(response);
    }catch (Exception e){
        System.out.println(e.getMessage());
    }


    }


}
