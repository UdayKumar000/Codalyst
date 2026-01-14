package com.company.demo.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

public class FileFilterUtils {
    private static final Set<String> EXCLUDED_DIRS = Set.of(
            ".git", "node_modules", "target", "build", "dist", ".idea", ".vscode", ".mvn"
    );

    private static final Set<String> EXCLUDED_EXTENSIONS = Set.of(
            ".png", ".jpg", ".jpeg", ".gif", ".pdf", ".zip", ".exe", ".jar", ".gitattributes", ".gitignore", "mvnw", ".cmd"
    );

    public boolean isExcluded(Path path){
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
}
