package com.company.demo.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

public class DirectoryDeleter {

    public static void deleteDirectory(Path dir) throws IOException {
        if (Files.notExists(dir)) return;

        int retries = 10;
        for(int i = 0; i < retries; i++) {


            try {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder()) // delete children first
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to delete " + path, e);
                            }
                        });

            } catch (Exception e) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                }
            }
        }

    }
}
