package com.company.demo.utils;
import java.io.*;
import java.nio.file.*;
import java.util.Set;
import java.util.stream.Stream;

public class CodePacker {

    private static final Set<String> EXCLUDED_DIRS = Set.of(
            ".git", "node_modules", "target", "build", "dist", ".idea", ".vscode", ".mvn"
    );

    private static final Set<String> EXCLUDED_EXTENSIONS = Set.of(
            ".png", ".jpg", ".jpeg", ".gif", ".pdf", ".zip", ".exe", ".jar", ".gitattributes", ".gitignore", "mvnw", ".cmd"
    );

    public static boolean isExcluded(Path path){
        String p = path.toString().toLowerCase();

        for (String dir : EXCLUDED_DIRS) {
            if (p.contains(File.separator + dir + File.separator)) {
                return true;
            }
        }

        for (String ext : EXCLUDED_EXTENSIONS) {
            if (p.endsWith(ext)) {
                return true;
            }
        }

        return false;
    }


    public static void packCode(String sourcePath,String outputPath){


        Path sourceDir = Paths.get(sourcePath);
        Path outputFile = Paths.get(outputPath);

        //
        Path parentDir = outputFile.getParent();
        if (parentDir != null) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create output directory", e);
            }
        }


        try(BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            try(Stream<Path> paths = Files.walk(sourceDir)){
                paths
                        .filter(Files::isRegularFile)
                        .filter(path -> !CodePacker.isExcluded(path))
//                        .filter(path -> path.toString().endsWith(".java"))
                        .forEach(path -> {
                            try{
                                writer.write("<file path=\"" + path + "\">\n");
                                String content = Files.readString(path);
//                                String cleaned = cleanCode(content);
                                writer.write(content);
                                writer.write("\n</file>\n\n");
                            }catch (IOException e) {
                                System.err.println("Could not read: " + path);
                            }
                        });

            }
        }catch(IOException e){
//            System.out.println(e.getMessage());
            System.err.println("Could not write output file");
        }

    }


    private static String cleanCode(String code) {
        // 1. Remove Multi-line comments (/* ... */)
        code = code.replaceAll("(?s)/\\*.*?\\*/", "");

        // 2. Remove Single-line comments (// ...)
        code = code.replaceAll("//.*", "");

        // 3. Remove Imports and Package declarations (AI usually doesn't need these)
        code = code.replaceAll("(?m)^import\\s+.*;", "");
        code = code.replaceAll("(?m)^package\\s+.*;", "");

        // 4. Remove empty lines to compact the text
        code = code.replaceAll("(?m)^\\s*\\r?\\n", "");

        return code.trim();
    }


}
