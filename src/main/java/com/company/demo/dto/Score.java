package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Score {
    private int x;
    private int y;
    @JsonProperty("label")
    private String fileName;
}
