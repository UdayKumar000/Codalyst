package com.company.demo.exceptions;

import com.company.demo.globalexception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ScriptGenerationException extends ApplicationException {
    public ScriptGenerationException(String message,Throwable cause) {
        super("ERROR",message, HttpStatus.BAD_REQUEST);

    }

}
