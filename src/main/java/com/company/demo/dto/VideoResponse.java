package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoResponse {

    @JsonProperty("project_id")
    Long projectId;

    @JsonProperty("video_id")
    String videoId;
}
