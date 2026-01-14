package com.company.demo.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProjectMapper {

    private static final Set<String> EXCLUDED_DIRS = Set.of(
            ".git", "node_modules", "target", "build", "dist", ".idea", ".vscode", ".mvn", "config", "head", "hooks", "index", "info", "logs", "objects", "packed-refs", "refs"
    );

    private static final Set<String> EXCLUDED_EXTENSIONS = Set.of(
            ".png", ".jpg", ".jpeg", ".gif", ".pdf", ".zip", ".exe", ".jar", ".gitattributes", ".gitignore", "mvnw", ".cmd"
    );

    public static boolean isExcluded(String path){

        for (String dir : EXCLUDED_DIRS) {
            if (path.contains(File.separator + dir + File.separator)) {
                return true;
            }
        }

        for (String ext : EXCLUDED_EXTENSIONS) {
            if (path.endsWith(ext)) {
                return true;
            }
        }

        return false;
    }

    // Folders to ignore to keep the AI prompt clean
//    private static final Set<String> IGNORE_LIST = new HashSet<>(Arrays.asList(
//            ".git", ".idea", "target", "build", ".settings", ".classpath", ".project", "bin"
//    ));

    public static void createProjectMap(String projectPath,String projectMapPath){
        File file = new File(projectPath);
        if(!file.exists()){
            return;
        }
        StringBuilder sb = new StringBuilder();
        generateTree(file,"",sb);
        Path destPath = Paths.get(projectMapPath);
        Path parent = destPath.getParent();

        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try(BufferedWriter writer = Files.newBufferedWriter(destPath)){
            writer.write(sb.toString());
        } catch (IOException e) {
            System.err.println("error while writing project_map"+e.getMessage());
        }
    }

    public static void generateTree(File folder,String indent, StringBuilder sb){
        File[] files = folder.listFiles();
        if(files==null || files.length==0) return;

        for(int i=0;i<files.length;i++){
            File file = files[i];
            if (isExcluded(file.getName())) continue;

            boolean isLast = (i==files.length-1);
            sb.append(indent).append(isLast ? "└── " : "├── ").append(file.getName()).append("\n");

            if (file.isDirectory()) {
                generateTree(file, indent + (isLast ? "    " : "│   "), sb);
            }

        }

    }




}
