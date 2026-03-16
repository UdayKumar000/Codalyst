package com.company.codelyst.exceptions;

import com.company.codelyst.globalexception.ApplicationException;
import org.springframework.http.HttpStatus;

public class GitOperationException extends ApplicationException {
    public GitOperationException(String message,Throwable cause) {
        super("ERROR",message, HttpStatus.BAD_REQUEST);

    }

}
