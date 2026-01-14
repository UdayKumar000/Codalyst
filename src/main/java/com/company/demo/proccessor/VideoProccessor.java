package com.company.demo.proccessor;

import lombok.Data;

@Data
public class VideoProccessor {
    private String repoUrl;
    private String repoPath;
    private String xmlFilePath;
    private String scriptFilePath;
    private String projectMapPath;
    public VideoProccessor(String repoUrl){
        this.repoUrl = repoUrl;
        this.repoPath = "projects"+repoUrl.substring(18);
        this.xmlFilePath = "projectXMLs"+repoUrl.substring(18)+"/all_code_files.txt";
        this.scriptFilePath = "projectScripts"+repoUrl.substring(18)+"/script.txt";
        this.projectMapPath = "projectMaps"+repoUrl.substring(18)+"/project_map.txt";
    }
}
