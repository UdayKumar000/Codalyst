package com.company.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Background {
    private String type;

    @JsonProperty("play_style")
    private String playStyle;
    private String fit;

    @JsonProperty("image_asset_id")
    private String imageAssetId;
}
