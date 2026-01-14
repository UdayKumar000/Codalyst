package com.company.demo.services;

import com.company.demo.gemini.HeyGenVideoGenerator;
import com.company.demo.gemini.ScriptCreator;
import com.company.demo.proccessor.VideoProccessor;
import com.company.demo.utils.CodePacker;
import com.company.demo.utils.GitClone;
import com.company.demo.utils.ProjectMapper;
import org.springframework.stereotype.Service;

@Service
public class GenerateVideo {


    public void generateVideo(VideoProccessor videoProccessor) throws Exception {

        //clone url
        GitClone.cloneRepository(videoProccessor.getRepoUrl(),videoProccessor.getRepoPath());
        // generate project map
        ProjectMapper.createProjectMap(videoProccessor.getRepoPath(),videoProccessor.getProjectMapPath());
        //pack the code
        CodePacker.packCode(videoProccessor.getRepoPath(),videoProccessor.getXmlFilePath());
        //generate script
        ScriptCreator.generateScript(videoProccessor.getXmlFilePath(),videoProccessor.getScriptFilePath());
        //generate video
        HeyGenVideoGenerator.generateVideo(videoProccessor.getScriptFilePath());


    }




}
