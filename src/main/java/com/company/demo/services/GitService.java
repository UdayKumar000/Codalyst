package com.company.demo.services;

import com.company.demo.exceptions.GitOperationException;
import com.company.demo.utils.DirectoryDeleter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GitService {

    private static final int CLONE_TIMEOUT_MINUTES = 5;

    public void cloneRepository(String repoUrl, String targetDir){

        validateRepoUrl(repoUrl);

        Path clonePath = Paths.get(targetDir);

        try{
            if (Files.exists(clonePath)) {
                DirectoryDeleter.deleteDirectory(clonePath);
                log.info("Repository already exists at {}, skipping clone", targetDir);
            }

            if (clonePath.getParent() != null) {
                Files.createDirectories(clonePath.getParent());
            }

//            Files.createDirectories(clonePath.getParent() != null
//                    ? clonePath.getParent()
//                    : clonePath);

            log.info("Cloning repository {} into {}", repoUrl, targetDir);

            Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(clonePath.toFile())
                    .setCloneAllBranches(false)
                    .setDepth(1)
                    .call()
                    .close();


            // Git clone command
//            ProcessBuilder pb = new ProcessBuilder(
//                    "git", "clone", "--depth", "1", repoUrl, targetDir
//            );


//            pb.redirectErrorStream(true);
//            Process process = pb.start();

            // Wait with timeout
//            boolean completed = process.waitFor(CLONE_TIMEOUT_MINUTES, TimeUnit.MINUTES);

//            if (!completed) {
//                process.destroyForcibly();
//                throw new GitOperationException("Git clone timed out for "+repoUrl,null);
//            }

//            if (process.exitValue() != 0) {
//                String errorOutput = new String(process.getInputStream().readAllBytes());
//                throw new GitOperationException("Git clone failed for "+repoUrl+". Error: "+errorOutput,null);
//            }

            log.info("Successfully cloned repository {}",repoUrl);
        }catch(GitAPIException e){
            log.error("JGit error cloning repository {}",repoUrl,e);
            throw new GitOperationException("JGit error cloning repository "+repoUrl,e);

        }
        catch(GitOperationException e){
            throw e;
        }catch (Exception e){
            log.error("Unexpected error while cloning repository {}",repoUrl,e);
            throw new GitOperationException("Unexpected error while cloning repository "+repoUrl,e);
        }



    }

    private void validateRepoUrl(String repoUrl) {
        if(repoUrl == null || !repoUrl.startsWith("https://github.com/"))
            throw new GitOperationException("Only GitHub repositories are allowed",null);
    }
}
