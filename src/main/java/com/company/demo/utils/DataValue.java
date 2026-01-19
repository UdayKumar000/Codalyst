package com.company.demo.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataValue {

    @JsonProperty("file_layer")
    private String fileLayer;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("x_score")
    private int xScore;
    @JsonProperty("y_score")
    private int yScore;
}
