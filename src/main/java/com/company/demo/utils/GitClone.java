package com.company.demo.utils;

import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Configuration
public class GitClone {
    public static void cloneRepository(String repoUrl, String targetDir) throws Exception  {

        Path clonePath = Paths.get(targetDir);

        // Create directory if doesn't exist
        if (!Files.exists(clonePath)) {
            Files.createDirectories(clonePath);
        }else{
            return;
        }

        // Git clone command
        ProcessBuilder pb = new ProcessBuilder(
                "git", "clone", "--depth", "1", repoUrl, targetDir
        );

        System.out.println("Command: " + String.join(" ", pb.command()));

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Wait with timeout
        boolean completed = process.waitFor(5, TimeUnit.MINUTES);

        if (!completed) {
            process.destroy();
            throw new RuntimeException("Git clone timed out");
        }

        if (process.exitValue() != 0) {
            throw new RuntimeException("Git clone failed: " +
                    new String(process.getInputStream().readAllBytes()));
        }

    }
}
