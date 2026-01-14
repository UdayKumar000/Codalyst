package com.company.demo.exceptions;

public class GitOperationException extends RuntimeException {
    public GitOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    public GitOperationException(String message) {
        super(message);
    }
}
