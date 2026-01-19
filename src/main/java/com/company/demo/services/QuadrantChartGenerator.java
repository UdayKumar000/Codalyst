package com.company.demo.services;

import com.company.demo.dto.QudrantResponse;
import com.company.demo.dto.Score;
import com.company.demo.exceptions.DatabaseExceptions;
import com.company.demo.exceptions.ScriptGenerationException;
import com.company.demo.models.Project;
import com.company.demo.models.ProjectQuadrantInfo;
import com.company.demo.repository.ProjectQuadrantInfoRepository;
import com.company.demo.repository.ProjectRepository;
import com.company.demo.utils.GeminiJsonResponse;
import com.company.demo.utils.Response;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class QuadrantChartGenerator {

    private final ProjectRepository projectRepository;
    private final ProjectQuadrantInfoRepository projectQuadrantInfoRepository;
    QuadrantInfoDatabaseService quadrantInfoDatabaseService;
    private final String apiKey;
    private final String modelName;

    public QuadrantChartGenerator(QuadrantInfoDatabaseService quadrantInfoDatabaseService, ProjectRepository projectRepository, @Value("${gemini.api.key}") String apiKey, @Value("${gemini.api.model:gemini-3-flash-preview}")  String modelName, ProjectQuadrantInfoRepository projectQuadrantInfoRepository) {
        this.projectRepository = projectRepository;
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.quadrantInfoDatabaseService = quadrantInfoDatabaseService;
        this.projectQuadrantInfoRepository = projectQuadrantInfoRepository;
    }

    public Response<QudrantResponse> getScoresFromProjectId (Long projectId){

        try{
            List<ProjectQuadrantInfo> info = projectQuadrantInfoRepository.findByProject_Id(projectId);

            if (info.isEmpty()) {
                throw new DatabaseExceptions("Project not found", null);
            }

            List<QudrantResponse> response = getQuadrantResponse(info);
            return new Response<>(true,response,"success");

        }catch (DatabaseExceptions e){
            throw e;
        }catch (Exception e){
            throw new DatabaseExceptions("Error : ",e);
        }


    }

    private List<QudrantResponse> getQuadrantResponse(List<ProjectQuadrantInfo> entities) {

        // 1. Group ProjectQuadrantInfo by fileLayer (this creates the separate datasets)
        Map<String, List<ProjectQuadrantInfo>> groupedData = entities.stream()
                .collect(Collectors.groupingBy(ProjectQuadrantInfo::getFileLayer));

        List<QudrantResponse> response = new ArrayList<>();

        // iterate through groups to build scores objects
        groupedData.forEach((layerName,infoList) -> {

            QudrantResponse temp = new QudrantResponse();
            temp.setFileLayer(infoList.get(0).getFileLayer());

            List<Score>  scoreList = new ArrayList<>();
            for(ProjectQuadrantInfo info : infoList){
                        Score score = new Score();
                        score.setX(info.getXScore());
                        score.setY(info.getYScore());
                        score.setFileName(info.getFileName());
                        scoreList.add(score);
                    }
            temp.setData(scoreList);
            response.add(temp);

        });

        return response;
    }

    public void generateJson (Long projectId){

        try{

            Project project = projectRepository.findById(projectId).orElseThrow(
                    () -> new DatabaseExceptions("Project not found ",null)
            );

            if(project.getXmlFileUrl()==null || project.getMapFileUrl()==null){
                throw new DatabaseExceptions("Project files not found ",null);
            }

            String projectMap = getMapFileContent(project.getMapFileUrl());
            String projectXml = getXmlFileContent(project.getXmlFileUrl());

            if(projectMap.isEmpty() || projectXml.isEmpty()){
                throw new ScriptGenerationException("project map and project xml are blank",null);
            }

            String prompt = buildPrompt(projectMap,projectXml);
            String jsonResponse = generateJsonReponse(prompt);


            if(jsonResponse == null || jsonResponse.isEmpty()){
                log.warn("Gemini API returned an empty scores.");
                throw new ScriptGenerationException("Empty script",null);
            }

            ObjectMapper objectMapper = new ObjectMapper();

            GeminiJsonResponse response = objectMapper.readValue(jsonResponse,GeminiJsonResponse.class);

            System.out.println(response.toString());

            log.info("Json Generated Successfully :");

            quadrantInfoDatabaseService.updateScoresToDatabase(projectId,response);

            log.info("Quadrant chart updated to database successfully. :");

        }catch(ScriptGenerationException e){
            log.error("Script Generation Failed : {}",e.getMessage());
            throw e;
        }


    }


    private String getXmlFileContent(String xmlFileUrl) {

        try{
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(xmlFileUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch file. Status: " + response.statusCode());
            }

            return response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private String getMapFileContent(String mapFileUrl) {

        try{
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mapFileUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch file. Status: " + response.statusCode());
            }
            return response.body();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    private String generateJsonReponse(String prompt){
        try (Client client = Client.builder().apiKey(apiKey).build()) {
            GenerateContentResponse response = client.models.generateContent(modelName, prompt, null);
            return response.text();
        } catch (Exception e) {
            log.error("Script Generation Failed : {}",e.getMessage());
            throw new ScriptGenerationException("Error while calling Gemini API ",e);
        }
    }

    private String buildPrompt(String projectMapFile,String projectXmlFile) {
        return """
                You are a strict Code Analysis Engine. Your goal is to analyze the provided code files and output a scoring matrix in a specific JSON format.
                INSTRUCTIONS:
                Analyze: Read through the provided code files.
                Filter: Only include files that belong to the following layers:
                Controllers
                Services
                Repositories
                Utils
                Middlewares
                Configurations
                DatabaseModels (or Models)
                Tests
                Ignore all other files.
                Score: Assign two scores (0-100) to each relevant file:
                x_score (Code Quality): Evaluate based on code cleanliness, proper error handling, naming conventions (camelCase/PascalCase), lack of hardcoded values, and efficiency. 100 is perfect quality.
                y_score (Business Logic): Evaluate the correctness and robustness of the business logic implementation. Do not judge based on the quantity or density of logic. Instead, assess if the logic is implemented correctly, if it solves the intended problem accurately, and if it is free from logical flaws. 100 means the business logic is perfectly implemented and correct. Lower scores indicate logical errors, buggy implementation, or logic that requires improvement.
                Format: Output ONLY a raw JSON array. Do not use Markdown formatting. Do not include any conversational text.
                OUTPUT SCHEMA: "data" : [ { "file_layer": "LayerName", "file_name": "FileName.extension", "x_score": 0, "y_score": 0 } ]
                INPUT FILES:
                Project Map File:
                %s
                Project XML File
                %s
                """.formatted(projectMapFile,projectXmlFile);
    }



}
