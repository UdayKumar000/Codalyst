package com.company.codelyst.controllers;

import com.company.codelyst.dto.VideoClientResponse;
import com.company.codelyst.services.VideoClient;
import com.company.codelyst.utils.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
