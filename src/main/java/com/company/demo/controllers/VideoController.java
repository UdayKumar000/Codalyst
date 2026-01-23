package com.company.demo.controllers;

import com.company.demo.dto.VideoClientResponse;
import com.company.demo.services.VideoClient;
import com.company.demo.utils.Response;
import com.google.genai.types.Video;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class VideoController {

    private final VideoClient videoClient;

    public VideoController( VideoClient videoClient) {
        this.videoClient = videoClient;
    }

    @GetMapping("/getExplainerVideo/{projectId}")
    public ResponseEntity<Response<VideoClientResponse>> retrieveVideo(@PathVariable Long projectId){
        return ResponseEntity.ok().body(new Response<>(true, List.of(videoClient.retrieveVideoFromProjectId(projectId)),"success"));
    }

}
