package com.company.demo.exceptions;

public class VideoClientException extends RuntimeException{
    public VideoClientException(String message, Throwable cause) {
        super(message, cause);
    }
    public VideoClientException(String message) {
        super(message);
    }
}
