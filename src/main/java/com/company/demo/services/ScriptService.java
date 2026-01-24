package com.company.demo.services;


import com.company.demo.exceptions.DatabaseExceptions;
import com.company.demo.exceptions.ScriptGenerationException;
import com.company.demo.models.Project;
import com.company.demo.repository.ProjectRepository;
import com.company.demo.utils.FetchCloudeFilesContent;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ScriptService {

    private final String apiKey;
    private final String modelName;
    private final ProjectRepository projectRepository;
    private final FetchCloudeFilesContent fetchCloudeFilesContent;

    public ScriptService(@Value("${gemini.api.key}") String apiKey, @Value("${gemini.api.model:gemini-3-flash-preview}")  String modelName, ProjectRepository projectRepository) {
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.projectRepository = projectRepository;
        this.fetchCloudeFilesContent = new FetchCloudeFilesContent();
    }

    public void generateScript (Long projectId,String filePath,String scriptFilePath){

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new DatabaseExceptions("Project not found ",null)
        );

        if(project.getXmlFileUrl()==null ){
            throw new DatabaseExceptions("Project xml file not found ",null);
        }

        String projectXmlFileContent = fetchCloudeFilesContent.getFileContentFromCloud(project.getXmlFileUrl());

        String prompt = buildPrompt(projectXmlFileContent);
        String script = generateScriptFromApi(prompt);


        if(script == null || script.isEmpty()){
            log.warn("Gemini API returned an empty script.");
            throw new ScriptGenerationException("Empty script",null);
        }

        writeScriptToFile(scriptFilePath,script);

        log.info("Script Generated Successfully : {}",scriptFilePath);

    }

    private void writeScriptToFile(String scriptFilePath, String script) {
        Path path = Paths.get(scriptFilePath);
        try {
            Path parentDir = path.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(script);
            }
        } catch (IOException e) {
            throw new ScriptGenerationException("Failed to write script to file: " + scriptFilePath, e);
        }



    }

    private String generateScriptFromApi(String prompt){
        try (Client client = Client.builder().apiKey(apiKey).build()) {
            GenerateContentResponse response = client.models.generateContent(modelName, prompt, null);
            return response.text();
        } catch (Exception e) {
            log.error("Script Generation Failed : {}",e.getMessage());
            throw new ScriptGenerationException("Error while calling Gemini API ",e);
        }
    }

    private String buildPrompt(String projectCode) {
        return """
                I have attached my Java project code in a structured format.
                I want to create a 2-minute explainer video.

                Instructions for you:
                1. Do NOT include any headings, markdown, tables, or numbered lists.
                2. Do NOT include any instructions, commentary, or extra text — only the spoken script.
                3. Write the entire narration as one continuous text, exactly as the avatar should speak.
                4. Make it clear, concise, and human-readable for an AI avatar to speak naturally.
                5. Include all the explanation, flow, and key points from the project, but in plain sentences.
                6. If there are any notable strengths or good practices in the project, briefly mention them in a polite and professional way.
                7. If there are any noticeable weaknesses or areas for improvement, mention them politely and constructively.
                8. If there are no clear strengths or weaknesses, completely skip this part and do not mention it.
                9. At the very end of the narration, include a brief code quality evaluation.
                10. Clearly state whether the overall code quality is good, average, or poor.
                11. If the code quality is good or average, mention an approximate percentage of code quality.
                12. If the code quality is poor or very bad, clearly state that it is poor and explain why in one or two sentences.
                13. If there are any critical concerns such as security issues, performance risks, bad design, or maintainability problems, mention them only in this final code quality section.
                14. Do not place the code quality evaluation anywhere else except at the end.
                15. Please generate the script content only for 1 to 1.5 minutes not more than that                
                
                Project Code:
                %s
                """.formatted(projectCode);
    }

}
