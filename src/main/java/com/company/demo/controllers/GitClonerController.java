package com.company.demo.controllers;

import com.company.demo.dto.UrlRequest;
import com.company.demo.dto.VideoResponse;
import com.company.demo.proccessor.VideoProccessor;
import com.company.demo.services.C4DiagramGeneratorService;
import com.company.demo.services.GenerateVideoService;
import com.company.demo.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@Slf4j
public class GitClonerController {

    GenerateVideoService generateVideoService;
    C4DiagramGeneratorService c4DiagramGeneratorService;

    public GitClonerController(GenerateVideoService generateVideoService, C4DiagramGeneratorService c4DiagramGeneratorService) {
        this.generateVideoService = generateVideoService;
        this.c4DiagramGeneratorService = c4DiagramGeneratorService;
    }

    @GetMapping("test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Server is running");
    }



    @PostMapping("/clone")
    public ResponseEntity<Response<VideoResponse>> gitClone( @RequestBody UrlRequest url) {
        log.info("Received clone request for repo: {}", url.getRepoUrl());

        if (url.getRepoUrl() == null || url.getRepoUrl().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new Response<>(false, null, "Repository URL is required"));
        }


        Response<VideoResponse> response = generateVideoService.generateVideo(url.getRepoUrl());
        if(response.isSuccess()){
            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.badRequest().body(response);

    }








}
