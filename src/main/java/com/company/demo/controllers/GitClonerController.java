package com.company.demo.controllers;

import com.company.demo.dto.UrlRequest;
import com.company.demo.gemini.RetrieveVideo;
import com.company.demo.proccessor.VideoProccessor;
import com.company.demo.services.GenerateVideo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class GitClonerController {

    GenerateVideo generatevideo;

    public GitClonerController(GenerateVideo generatevideo) {
        this.generatevideo = generatevideo;
    }

    @GetMapping("test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Server is running");
    }

    @GetMapping("retrieveVideo")
    public ResponseEntity<?> retrieveVideo() {
        RetrieveVideo.retrieveVideo();
        return ResponseEntity.ok("Server is running");
    }

    @PostMapping("/clone")
    public ResponseEntity<?> gitClone(@RequestBody UrlRequest url) throws Exception {
        VideoProccessor videoProccessor = new VideoProccessor(url.getRepoUrl());
        generatevideo.generateVideo(videoProccessor);
        return ResponseEntity.ok("Server is running");

    }
}
