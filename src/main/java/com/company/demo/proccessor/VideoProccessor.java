package com.company.demo.proccessor;

import lombok.Data;

import java.io.File;

@Data
public class VideoProccessor {
    private String repoUrl;
    private String tempRepoPath;
    private String xmlFilePath;
    private String scriptFilePath;
    private String projectMapPath;
    private String diagramsPath;
    public VideoProccessor(String repoUrl){
        this.repoUrl = repoUrl.replace(".git","");
        String tempDir = System.getProperty("java.io.tmpdir");

        this.tempRepoPath = tempDir+repoUrl.substring(19).replace("/", "\\");
        System.out.println(tempRepoPath);
        this.xmlFilePath = tempDir+repoUrl.substring(19)+"/all_code_files.txt".replace("/", "\\");
        this.scriptFilePath = tempDir+repoUrl.substring(19)+"/script.txt".replace("/", "\\");
        this.projectMapPath = tempDir+repoUrl.substring(19)+"/project_map.txt".replace("/", "\\");
        this.diagramsPath = tempDir+repoUrl.substring(19)+"/diagrams".replace("/", "\\");


    }
}
