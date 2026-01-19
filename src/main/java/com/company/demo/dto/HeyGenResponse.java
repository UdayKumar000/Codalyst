package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class HeyGenResponse {

    Object error;
    Data data;

    @lombok.Data
    @NoArgsConstructor
    public static class Data {
        @JsonProperty("video_id")
        String videoId;
    }

}

