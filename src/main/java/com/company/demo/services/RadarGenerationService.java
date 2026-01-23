package com.company.demo.services;

import com.company.demo.dto.RadarResponse;
import com.company.demo.exceptions.DatabaseExceptions;
import com.company.demo.exceptions.ScriptGenerationException;
import com.company.demo.models.Project;
import com.company.demo.models.ProjectRadarInfo;
import com.company.demo.repository.ProjectRadarInfoRepository;
import com.company.demo.repository.ProjectRepository;
import com.company.demo.utils.FetchCloudeFilesContent;
import com.company.demo.utils.GeminiJsonResponse;
import com.company.demo.utils.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RadarGenerationService {

    private final ProjectRepository projectRepository;
    private final String apiKey;
    private final String modelName;
    private final FetchCloudeFilesContent  fetchCloudeFilesContent;
    private final RadarDatabaseService radarDatabaseService;
    private final ProjectRadarInfoRepository  projectRadarInfoRepository;
    public RadarGenerationService(ProjectRepository projectRepository, @Value("${gemini.api.key}") String apiKey, @Value("${gemini.api.model:gemini-3-flash-preview}")String modelName, RadarDatabaseService radarDatabaseService, ProjectRadarInfoRepository projectRadarInfoRepository) {
        this.projectRepository = projectRepository;
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.radarDatabaseService = radarDatabaseService;
        this.projectRadarInfoRepository = projectRadarInfoRepository;
        this.fetchCloudeFilesContent = new FetchCloudeFilesContent();
    }

    public Response<RadarResponse> generateArrayAndGet (Long projectId){

        try{

            RadarResponse radarResponseExists = isDataAlreadyExists(projectId);

            if(radarResponseExists != null){
                return new Response<>(true,List.of(radarResponseExists),"success");
            }

            Optional<Project> project = projectRepository.findById(projectId);

            if(project.isEmpty()){
                throw new DatabaseExceptions("Project not found while generating json ",null);
            }

            String projectMap = fetchCloudeFilesContent.getFileContentFromCloud(project.get().getMapFileUrl());
            String projectXml = fetchCloudeFilesContent.getFileContentFromCloud(project.get().getXmlFileUrl());

            String prompt = buildPrompt(projectMap,projectXml);
            String arrayResponse = generateArrayResponse(prompt);


            if(arrayResponse == null || arrayResponse.isEmpty()){
                log.warn("Gemini API returned an empty scores.");
                throw new ScriptGenerationException("Empty array ",null);
            }

            ObjectMapper objectMapper = new ObjectMapper();

            RadarResponse radarResponse = objectMapper.readValue(arrayResponse,RadarResponse.class);

            log.info("Array Generated Successfully :");

            radarDatabaseService.updateToDatabase(projectId,radarResponse);

            log.info("Radar chart calculated . :");

            return new Response<RadarResponse>(true,List.of(radarResponse),"success");


        }catch(ScriptGenerationException e){
            log.error("Script Generation Failed : {}",e.getMessage());
            throw e;
        }
    }

    private RadarResponse isDataAlreadyExists(Long projectId){

        Optional<ProjectRadarInfo> projectRadarInfoOptional = projectRadarInfoRepository.findByProject_Id(projectId);

        if(projectRadarInfoOptional.isEmpty()){
            return null;
        }

        ProjectRadarInfo projectRadarInfo = projectRadarInfoOptional.get();

        List<Integer> dataArray =  new ArrayList<>();

        dataArray.add(projectRadarInfo.getCodeQuality());
        dataArray.add(projectRadarInfo.getArchitecture());
        dataArray.add(projectRadarInfo.getReliability());
        dataArray.add(projectRadarInfo.getPerformance());
        dataArray.add(projectRadarInfo.getSecurity());
        dataArray.add(projectRadarInfo.getTestability());

        RadarResponse radarResponse = new RadarResponse();
        radarResponse.setData(dataArray);

        return radarResponse;


    }


    private String generateArrayResponse(String prompt){
        try (Client client = Client.builder().apiKey(apiKey).build()) {
            GenerateContentResponse response = client.models.generateContent(modelName, prompt, null);
            return response.text();
        } catch (Exception e) {
            log.error("Array Generation Failed : {}",e.getMessage());
            throw new ScriptGenerationException("Error while calling Gemini API ",e);
        }
    }

    private String buildPrompt(String projectMapFile,String projectXmlFile) {
        return """
                You are an expert Project Auditor. Your goal is to analyze the provided project code files and calculate an overall health score (0-100) for the entire project across six specific metrics.
                INSTRUCTIONS:
                Analyze: Read all provided source code files to understand the project structure, logic, and quality.
                Metrics Definitions:
                Code Quality: Adherence to clean code principles, naming conventions, and readability.
                Architecture: Proper layering, separation of concerns, and modularity.
                Reliability: Robust error handling, null safety, and recovery mechanisms.
                Performance: Efficiency of algorithms, resource management (e.g., connection pools, memory), and potential bottlenecks.
                Security: Absence of hardcoded secrets, proper input validation, and secure communication patterns.
                Testability: Degree of decoupling and how easily the code can be unit-tested.
                Scoring: Assign a score from 0 to 100 for each metric, where 100 is the highest possible score.
                Format: Output ONLY a raw JSON array of 6 integers. The scores must be in the exact order: [Code Quality, Architecture, Reliability, Performance, Security, Testability]. Do not use Markdown formatting. Do not include any conversational text or explanations.
                OUTPUT FORMAT EXAMPLE:{ "data" : [85, 70, 95, 60, 50, 80]}
                INPUT FILES:
                Project Map File:
                %s
                Project XML File
                %s
                """.formatted(projectMapFile,projectXmlFile);
    }

}
