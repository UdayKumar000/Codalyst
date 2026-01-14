package com.company.demo.services;


import com.company.demo.exceptions.ScriptGenerationException;
import com.google.api.client.util.Value;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.script.ScriptException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ScriptService {

    private final String apiKey;
    private final String modelName;

    public ScriptService(@Value("${gemini.api.key}") String apiKey,@Value("${gemini.api.model:gemini-3-flash-preview}")  String modelName) {
        this.apiKey = apiKey;
        this.modelName = modelName;
    }

    public void generateScript (String filePath,String scriptFilePath){

        try{
            String projectCode = readCodeFile(filePath);
            String prompt = buildPrompt(projectCode);
            String script = generateScriptFromApi(prompt);

            if(script == null || script.isEmpty()){
                log.warn("Gemini API returned an empty script.");
                return;
            }

            writeScriptToFile(scriptFilePath,script);
            log.info("Script Generated Successfully : {}",scriptFilePath);
        }catch(ScriptGenerationException e){
            log.error("Script Generation Failed : {}",e.getMessage());
            throw e;
        }


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
            throw new ScriptGenerationException("Error while calling Gemini API", e);
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

                Project Code:
                %s
                """.formatted(projectCode);
    }

    private String readCodeFile(String filePath) {
        Path path = Paths.get(filePath);
        try{
            if(!Files.exists(path) || !Files.isReadable(path)){
                throw new ScriptGenerationException("Code file does not exists or readable "+filePath);
            }
            return Files.readString(path);
        }catch(IOException e){
            throw new ScriptGenerationException("Error while reading file",e);
        }
    }

}
