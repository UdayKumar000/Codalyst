package com.company.demo.services;

import com.company.demo.exceptions.DatabaseExceptions;
import com.company.demo.exceptions.FileProcessingException;
import com.company.demo.exceptions.KrokiException;
import com.company.demo.exceptions.ScriptGenerationException;
import com.company.demo.models.Project;
import com.company.demo.models.ProjectC4Diagrams;
import com.company.demo.repository.ProjectC4DiagramsRepository;
import com.company.demo.repository.ProjectRepository;
import com.company.demo.utils.FetchCloudeFilesContent;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class C4DiagramGeneratorService {

    private final ProjectRepository projectRepository;
    private final String apiKey;
    private final String modelName;
    private final KrokiSvgGenerator krokiSvgGenerator;
    private final ProjectC4DiagramsRepository  projectC4DiagramsRepository;
    private final FetchCloudeFilesContent  fetchCloudeFilesContent;

    public C4DiagramGeneratorService(ProjectRepository projectRepository, @Value("${gemini.api.key}") String apiKey, @Value("${gemini.api.model:gemini-3-flash-preview}")  String modelName, KrokiSvgGenerator krokiSvgGenerator, ProjectC4DiagramsRepository projectC4DiagramsRepository) {
        this.projectRepository = projectRepository;
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.krokiSvgGenerator = krokiSvgGenerator;
        this.projectC4DiagramsRepository = projectC4DiagramsRepository;
        this.fetchCloudeFilesContent = new FetchCloudeFilesContent();
    }

    public String c4SVGGenerator(Long projectId){


        KrokiException lastException = null;

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new DatabaseExceptions("Project not found",null)
        );


        Optional<ProjectC4Diagrams> projectC4DiagramsOptional = projectC4DiagramsRepository.findByProjectId(projectId);
        ProjectC4Diagrams projectC4Diagrams = projectC4DiagramsOptional.orElse(new ProjectC4Diagrams());

        if(projectC4DiagramsOptional.isPresent()){
            if(projectC4DiagramsOptional.get().getC4DiagramUrl()!=null){
                return projectC4DiagramsOptional.get().getC4DiagramUrl();
            }
        }

        String mmdFileContent = c4DiagramCodeGenerator(project.getMapFileUrl(),project.getXmlFileUrl());

        for(int i=0;i<5;i++){

            try{

                String svgFileUrl = krokiSvgGenerator.getSvg(mmdFileContent);

                projectC4Diagrams.setC4DiagramUrl(svgFileUrl);
                projectC4Diagrams.setProject(project);

                projectC4DiagramsRepository.save(projectC4Diagrams);

                return svgFileUrl;

            }catch(KrokiException e){
                lastException = e;

                String krokiError = e.getMessage();
                log.warn("Retry {} failed due to Kroki error", i + 1);

                String regeneratedPrompt =
                        reGeneratePrompt(mmdFileContent, krokiError);

                mmdFileContent = generateMmdFile(regeneratedPrompt);

            }catch (Exception e){
                log.error("Unknown error in c4SVGGenerator failed due to {}",e.getMessage());
                throw e;
            }
        }
        throw new FileProcessingException("C4 SVG generation failed after retries", lastException);
    }



    private String c4DiagramCodeGenerator(String mapFileUrl,String xmlFileUrl){

        try {

            String projectMap = fetchCloudeFilesContent.getFileContentFromCloud(mapFileUrl);
            String projectXml = fetchCloudeFilesContent.getFileContentFromCloud(xmlFileUrl);

            String prompt = generatePrompt(projectMap,projectXml);

            String mmdFile = generateMmdFile(prompt);

            if(mmdFile == null || mmdFile.isEmpty()){
                log.warn("Gemini API returned an empty mmd file.");
                throw new FileProcessingException("Empty mmd file",null);
            }

            return mmdFile;

        }catch(ScriptGenerationException | FileProcessingException e){
            throw e;
        }
        catch (Exception e) {
            throw new FileProcessingException("Unknown error File not found",e);
        }


    }



    private String generateMmdFile(String prompt) {

        try (Client client = Client.builder().apiKey(apiKey).build()) {
            GenerateContentResponse response = client.models.generateContent(modelName, prompt, null);
            return response.text();
        } catch (Exception e) {
            log.error("mmd file Generation Failed : {}",e.getMessage());
            throw new ScriptGenerationException("Error while calling Gemini API ",e);
        }
    }


    private String reGeneratePrompt(String mmdFile,String error) {
        return """
            The Mermaid.js script you generated previously contains syntax errors and failed to render.
            
            ### ERROR FROM RENDERER:
            %s
            
            ### ORIGINAL GENERATED CODE:
            ```mermaid
            %s
            ```
            
            ### INSTRUCTIONS:
            1. Analyze the error message and the original code to identify the syntax mistake.
            2. Re-generate the entire Mermaid graph script.
            3. Ensure you strictly follow the 'graph TB' flowchart syntax.
            4. Do NOT use C4-specific syntax (like System_Boundary or Person); use standard 'subgraph' blocks.
            5. Ensure all node IDs are valid (no spaces or special characters unless enclosed in double quotes).
            6. Provide ONLY the corrected Mermaid code block.
            7. OUTPUT ONLY THE RAW CODE.
            8. DO NOT include markdown formatting like ```mermaid or ``` tags.
            9. DO NOT include any explanations, headers, or footers.
            """.formatted(error, mmdFile);
    }

    private String generatePrompt(String projectMap, String projectXml) {
        return """
                Act as a Software Architect and generate a Mermaid.js script for a system architecture diagram.
                Requirements:
                1. Format: You must use ONLY the standard Mermaid graph TB flowchart syntax. Do not use C4Context, C4Container, or any specialized C4 Model functions like Person() or System().
                2. Analysis: Analyze the provided project structure and code files to identify relationships (imports, API calls, and data flow).
                3. Structure: Organize the diagram using subgraph blocks to create the following logical boundaries:
                    - Frontend_Boundary (React/Vite)
                    - Backend_Boundary (Express/Node)
                    - Database_Boundary (MongoDB)
                4. Logic Layers: Within the Backend_Boundary, create nested subgraphs for:
                    - Routes
                    - Middlewares
                    - Controllers
                    - Services
                    - Models
                5. Relationships: Draw connection lines (-->) to show how components interact (e.g., UI calls the App, Routes use Middlewares, Controllers call Services, Services interact with Models).
                6. Styling: Include style definitions at the end of the script to color the main boundaries:
                    - Frontend_Boundary: #f9f
                    - Backend_Boundary: #bbf
                    - Database_Boundary: #dfd
                7. Output: Provide ONLY the Mermaid code block starting with graph TB. Do not include any introductory or explanatory text.
                Please process the files and generate the code now.
                Here are contents of the files
                <projectmapping>
                %s
                </projectmapping>
                
                <projectfilesxml>
                %s
                </projectfilesxml>
                """.formatted(projectMap,projectXml);
    }

}
