package com.company.codelyst.exceptions;

import com.company.codelyst.globalexception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CodePackException extends ApplicationException {
    public CodePackException(String message,Throwable cause) {
        super("ERROR",message, HttpStatus.BAD_REQUEST);

    }

}
