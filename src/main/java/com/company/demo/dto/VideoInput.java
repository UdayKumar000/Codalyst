package com.company.demo.dto;

import lombok.Data;

@Data
public class VideoInput{
    private Character character;
    private Voice voice;
    private Background background;
    private Text text;
}
