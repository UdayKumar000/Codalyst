package com.company.demo.exceptions;

import com.company.demo.globalexception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String message,Throwable cause) {
        super("ERROR",message, HttpStatus.BAD_REQUEST);

    }
}

