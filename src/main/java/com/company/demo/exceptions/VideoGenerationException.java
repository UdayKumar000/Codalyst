package com.company.demo.exceptions;

public class VideoGenerationException extends RuntimeException {

    public VideoGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
    public VideoGenerationException(String message) {
        super(message);
    }
}
