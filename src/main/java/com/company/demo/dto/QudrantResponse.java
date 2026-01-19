package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class QudrantResponse {

    @JsonProperty("label")
    String fileLayer;
    List<Score> data;
}
