package com.company.demo.globalexception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.Instant;


@Getter
public class ApiError {

    private String errorCode;
    private String message;
    private int status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    public ApiError(String errorCode, String message, int status, Instant timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

}
