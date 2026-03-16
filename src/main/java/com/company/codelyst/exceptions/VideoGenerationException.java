package com.company.codelyst.exceptions;

import com.company.codelyst.globalexception.ApplicationException;
import org.springframework.http.HttpStatus;

public class VideoGenerationException extends ApplicationException {


    public VideoGenerationException(String message,Throwable cause) {
        super("ERROR",message, HttpStatus.BAD_REQUEST);

    }
}
