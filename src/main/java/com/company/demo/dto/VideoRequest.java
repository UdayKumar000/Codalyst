package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VideoRequest {

    private String caption;

    @JsonProperty("video_inputs")
    private List<VideoInput> videoInputs;
    private Dimension dimension;

}

