package com.company.demo.exceptions;

public class CodePackException extends RuntimeException{
    public CodePackException(String message) {
        super(message);
    }
    public CodePackException(String message, Throwable cause) {
        super(message, cause);
    }
}
