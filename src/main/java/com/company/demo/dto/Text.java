package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Text {
    private String type;
    private Position position;
    private String text;

    @JsonProperty("line_height")
    private int lineHeight;
}
