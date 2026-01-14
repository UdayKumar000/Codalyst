package com.company.demo.services;
import com.company.demo.exceptions.CodePackException;
import com.company.demo.utils.FileFilterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
@Slf4j
public class CodePackService {

    private final FileFilterUtils fileFilterUtils = new FileFilterUtils();

    public void packCode(String sourcePath,String outputPath){

        Path sourceDir = Paths.get(sourcePath);
        Path outputFile = Paths.get(outputPath);

        validateSourceDirectory(sourceDir);
        createOutputDirectory(outputFile);

        log.info("Packing source code from {} into {}",sourceDir,outputFile);


        try(BufferedWriter writer = Files.newBufferedWriter(outputFile);
            Stream<Path> paths = Files.walk(sourceDir);) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(path -> !fileFilterUtils.isExcluded(path))
                    .forEach(path -> writeFileContent(writer,sourceDir,path));

            log.info("Code packing completed successfully");


        }catch(IOException e){
            log.error("Failed to pack code", e);
            throw new CodePackException("Failed to pack source code", e);
        }

    }

    private void writeFileContent(BufferedWriter writer, Path sourceDir, Path path) {
        try {
            writer.write("<file path=\"" + sourceDir.relativize(path) + "\">\n");
            writer.write(Files.readString(path));
            writer.write("\n</file>\n\n");
        } catch (IOException e) {
            log.warn("Skipping file due to read error: {}", path, e);
//            throw new CodePackException("Failed to read file: " + path, e);
        }
    }

    private void createOutputDirectory(Path outputPath) {
        try{
            Path parentDir = outputPath.getParent();
            if(parentDir != null){
                Files.createDirectories(parentDir);
            }
        }catch(IOException e){
            throw new CodePackException("Failed to create output directory", e);
        }
    }

    private void validateSourceDirectory(Path sourceDir) {
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new CodePackException("Source directory does not exist: " + sourceDir);
        }
    }


//    private static String cleanCode(String code) {
//        // 1. Remove Multi-line comments (/* ... */)
//        code = code.replaceAll("(?s)/\\*.*?\\*/", "");
//
//        // 2. Remove Single-line comments (// ...)
//        code = code.replaceAll("//.*", "");
//
//        // 3. Remove Imports and Package declarations (AI usually doesn't need these)
//        code = code.replaceAll("(?m)^import\\s+.*;", "");
//        code = code.replaceAll("(?m)^package\\s+.*;", "");
//
//        // 4. Remove empty lines to compact the text
//        code = code.replaceAll("(?m)^\\s*\\r?\\n", "");
//
//        return code.trim();
//    }


}
