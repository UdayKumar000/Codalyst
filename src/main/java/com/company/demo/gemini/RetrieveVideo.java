package com.company.demo.gemini;

import com.company.demo.webclient.ConfigWebClient;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

public class RetrieveVideo {


    private static final WebClient webClient = ConfigWebClient.webClient();

    public static void retrieveVideo(){

        JsonNode json = webClient.get().uri(uriBuilder ->
                    uriBuilder
                            .path("/v1/video_status.get")
                            .queryParam("video_id","f43bbcba867f4842b50607471894e464")
                            .build()
            ).retrieve().bodyToMono(JsonNode.class).block();
        System.out.println(json);
    }
}
