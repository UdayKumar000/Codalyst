package com.company.codelyst.controllers;

import com.company.codelyst.dto.UrlRequest;
import com.company.codelyst.dto.VideoResponse;
import com.company.codelyst.services.GenerateVideoService;
import com.company.codelyst.services.UrlService;
import com.company.codelyst.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@Slf4j
public class GitClonerController {

    private final GenerateVideoService generateVideoService;
    private final UrlService urlService;

    public GitClonerController(GenerateVideoService generateVideoService, UrlService urlService) {
        this.generateVideoService = generateVideoService;
        this.urlService = urlService;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Server is running");
    }


    @GetMapping("/getRepoUrl/{projectId}")
    public ResponseEntity<Response<String>> getRepoUrl(@PathVariable Long projectId) {
        String repoUrl = urlService.getUrlFromProjectId(projectId);
        return ResponseEntity.ok().body(new Response<>(true, List.of(repoUrl),"success"));
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
