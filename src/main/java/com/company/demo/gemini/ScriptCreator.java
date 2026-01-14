package com.company.demo.gemini;


import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScriptCreator {
    public static void generateScript (String filePath,String scriptFilePath){

        Client client = Client.builder()
                .apiKey("AIzaSyB5GVkqpT1PALspU8K04cuXLWuni-QdiXc")
                .build();

        try {
            // 2. Load your packed and cleaned code from Phase 1
//            String filePath = "all_code_files.txt";
            String projectCode = new String(Files.readAllBytes(Paths.get(filePath)));

            // 3. Construct the "Phase 2" Prompt

            String prompt = "I have attached my Java project code in a structured format.\n" +
                    "I want to create a 2-minute explainer video.\n" +
                    "\n" +
                    "Instructions for you:\n" +
                    "1. Do NOT include any headings, markdown, tables, or numbered lists.\n" +
                    "2. Do NOT include any instructions, commentary, or extra text — only the spoken script.\n" +
                    "3. Write the entire narration as one continuous text, exactly as the avatar should speak.\n" +
                    "4. Make it clear, concise, and human-readable for an AI avatar to speak naturally.\n" +
                    "5. Include all the explanation, flow, and key points from the project, but in plain sentences.\n" +
                    "6. If there are any notable strengths or good practices in the project, briefly mention them in a polite and professional way.\n" +
                    "7. If there are any noticeable weaknesses or areas for improvement, mention them politely and constructively.\n" +
                    "8. If there are no clear strengths or weaknesses, completely skip this part and do not mention it.\n" +
                    "9. At the very end of the narration, include a brief code quality evaluation.\n" +
                    "10. Clearly state whether the overall code quality is good, average, or poor.\n" +
                    "11. If the code quality is good or average, mention an approximate percentage of code quality.\n" +
                    "12. If the code quality is poor or very bad, clearly state that it is poor and explain why in one or two sentences.\n" +
                    "13. If there are any critical concerns such as security issues, performance risks, bad design, or maintainability problems, mention them only in this final code quality section.\n" +
                    "14. Do not place the code quality evaluation anywhere else except at the end.\n" +
                    "\n" +
                    "Project Code:\n" + projectCode;

//            String prompt = "I have attached my Java project code in a structured format.\n" +
//                    "I want to create a 2-minute explainer video.\n" +
//                    "\n" +
//                    "Instructions for you:\n" +
//                    "1. Do NOT include any headings, markdown, tables, or numbered lists.\n" +
//                    "2. Do NOT include any instructions, commentary, or extra text — only the spoken script.\n" +
//                    "3. Write the entire narration as one continuous text, exactly as the avatar should speak.\n" +
//                    "4. Make it clear, concise, and human-readable for an AI avatar to speak naturally.\n" +
//                    "5. Include all the explanation, flow, and key points from the project, but in plain sentences.\n" +
//                    "6. At the very end of the narration, include a brief code quality evaluation.\n" +
//                    "7. Clearly state whether the overall code quality is good, average, or poor.\n" +
//                    "8. If the code quality is good or average, mention an approximate percentage of code quality.\n" +
//                    "9. If the code quality is poor or very bad, clearly state that it is poor and explain why in one or two sentences.\n" +
//                    "10. If there are any critical concerns such as security issues, performance risks, bad design, or maintainability problems, mention them only in this final code quality section.\n" +
//                    "11. Do not place the code quality evaluation anywhere else except at the end.\n" +
//                    "\n" +
//                    "Project Code:\n" + projectCode;

//            String prompt = "I have attached my Java project code in a structured format. \n" +
//                    "I want to create a 2-minute explainer video.\n" +
//                    "\n" +
//                    "Instructions for you:\n" +
//                    "1. Do NOT include any headings, markdown, tables, or numbered lists.\n" +
//                    "2. Do NOT include any instructions, commentary, or extra text — only the spoken script.\n" +
//                    "3. Write the entire narration as one continuous text, exactly as the avatar should speak.\n" +
//                    "4. Make it clear, concise, and human-readable for an AI avatar to speak naturally.\n" +
//                    "5. Include all the explanation, flow, and key points from the project, but in plain sentences.\n" +
//                    "6. \n" +
//                    "Project Code:\n" + projectCode;



            GenerateContentResponse response = client.models.generateContent(
                    "gemini-3-flash-preview",
                    prompt,
                    null);
            String script = response.text();
            Path scriptFile = Paths.get(scriptFilePath);

            // create script directory
            Path parentDir = scriptFile.getParent();
            if (parentDir != null) {
                try {
                    Files.createDirectories(parentDir);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create output directory", e);
                }
            }

            // write the script file
            try(BufferedWriter writer = Files.newBufferedWriter(scriptFile)) {
                if(script!=null) {
                    writer.write(script);
                }else{
                    System.out.println("No script file provided");
                }
            }catch(IOException e){
                System.out.println(e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("Error reading the code file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
        }

    }

}
