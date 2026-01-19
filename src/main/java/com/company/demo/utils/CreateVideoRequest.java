package com.company.demo.utils;

import com.company.demo.dto.*;
import com.company.demo.dto.Character;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CreateVideoRequest {

    public static VideoRequest generateVideoRequest (String scriptFilePath){

        VideoRequest request = new VideoRequest();

        Path path = Paths.get(scriptFilePath);
        String repoName = path.getParent().getParent().getFileName().toString();

        if(!Files.exists(path)){
            throw new IllegalArgumentException("");
        }
        String script = "";
        try {
            script = Files.readString(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if(script.isBlank()){
            throw new IllegalArgumentException("");
        }

        com.company.demo.dto.Character character = new Character();
        character.setType("avatar");
        character.setScale(1);
        character.setAvatarId("Abigail_expressive_2024112501");
        character.setAvatarStyle("normal");
        character.setTalkingPhotoId("6013fc758b5446a2ba17d8c459538bb4");
        character.setTalkingStyle("stable");
        character.setExpression("happy");

        Voice voice = new Voice();
        voice.setType("text");
        voice.setInputText(script);
        voice.setVoiceId("f8c69e517f424cafaecde32dde57096b");
        voice.setSpeed("1");
        voice.setDuration(1);
        voice.setPitch(0);

//        Background background = new Background();
//        background.setType("image");
//        background.setFit("cover");
//        background.setImageAssetId("ad0e3dc084ed43ffa35fb6de889a695c");
//        background.setPlayStyle("freeze");

        Position position = new Position();
        position.setX(0);
        position.setX(0);

        Text text = new Text();
        text.setPosition(position);
        text.setType("text");
        text.setLineHeight(1);
        text.setText(repoName);

        VideoInput videoInput = new VideoInput();
        videoInput.setCharacter(character);
        videoInput.setVoice(voice);
//        videoInput.setBackground(background);
        videoInput.setText(text);

        Dimension dimension = new Dimension();
        dimension.setWidth(1280);
        dimension.setHeight(720);

        request.setCaption("true");
        request.setVideoInputs(List.of(videoInput));
        request.setDimension(dimension);

        return request;
    }
}
