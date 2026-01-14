package com.company.demo.exceptions;

public class ScriptGenerationException extends RuntimeException {
    public ScriptGenerationException(String message){
        super(message);
    }
    public ScriptGenerationException(String message, Throwable cause){
        super(message,cause);
    }
}
