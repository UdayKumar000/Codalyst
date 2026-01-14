package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Character{
    private String type;
    private int scale;

    @JsonProperty("talking_style")
    private String talkingStyle;

    @JsonProperty("talking_photo_id")
    private String talkingPhotoId;

    @JsonProperty("avatar_id")
    private String avatarId;

    @JsonProperty("avatar_style")
    private String avatarStyle;
    private String expression;
}