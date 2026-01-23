package com.company.demo.controllers;

import com.company.demo.dto.QudrantResponse;
import com.company.demo.dto.RadarResponse;
import com.company.demo.services.C4DiagramGeneratorService;
import com.company.demo.services.QuadrantChartGenerator;
import com.company.demo.services.RadarGenerationService;
import com.company.demo.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
@Slf4j
public class VisualsController {

    private final QuadrantChartGenerator quadrantChartGenerator;
    private final RadarGenerationService radarGenerationService;
    private final C4DiagramGeneratorService c4DiagramGeneratorService;

    public VisualsController(QuadrantChartGenerator quadrantChartGenerator, RadarGenerationService radarGenerationService, C4DiagramGeneratorService c4DiagramGeneratorService) {
        this.quadrantChartGenerator = quadrantChartGenerator;
        this.radarGenerationService = radarGenerationService;
        this.c4DiagramGeneratorService = c4DiagramGeneratorService;
    }

    @GetMapping("/getRadarChart/{projectId}")
    public ResponseEntity<Response<RadarResponse>> radarChart(@PathVariable Long projectId) {
        log.info("Received getRadarChart request for projectId: {}", projectId);
        Response<RadarResponse> response = radarGenerationService.generateArrayAndGet(projectId);
            return ResponseEntity.ok().body(response);
    }


    @GetMapping("/getQuadrantChart/{projectId}")
    public ResponseEntity<Response<QudrantResponse>> quadrantChart(@PathVariable Long projectId) {
        log.info("Received quadrantChart request for projectId: {}", projectId);
        Response<QudrantResponse> response = quadrantChartGenerator.getScoresIfAlreadyExists(projectId);
        if(response!=null){
            return ResponseEntity.ok().body(response);
        }
        quadrantChartGenerator.generateJson(projectId);
        response = quadrantChartGenerator.getScoresFromProjectId(projectId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getC4Diagram/{projectId}")
    public ResponseEntity<Response<String>> getSvgDiagram(@PathVariable Long projectId) {
        log.info("Received getSvgDiagram request for projectId: {}", projectId);
        String svgFileUrl = c4DiagramGeneratorService.c4SVGGenerator(projectId);
        Response<String> response = new Response<>(true,List.of(svgFileUrl),"success");
        return ResponseEntity.ok().body(response);
    }
}
