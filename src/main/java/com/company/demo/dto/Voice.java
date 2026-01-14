package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Voice{
    private String type;

    @JsonProperty("input_text")
    private String inputText;

    @JsonProperty("voice_id")
    private String voiceId;
    private int duration;
    private String speed;
    private int pitch;
}
