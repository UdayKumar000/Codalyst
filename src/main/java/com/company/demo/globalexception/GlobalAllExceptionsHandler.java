package com.company.demo.globalexception;

import com.company.demo.globalexception.ApiError;
import com.company.demo.globalexception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice("hel")
@Slf4j
public class GlobalAllExceptionsHandler {

    // 1️⃣ Handle known business exceptions
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiError> handleApplicationException(ApplicationException ex) {

        log.error("Application error: {}", ex.getMessage(), ex);

        ApiError error = new ApiError(
                ex.getErrorCode(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    // 2️⃣ Validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ApiError error = new ApiError(
                "VALIDATION_ERROR",
                message,
                HttpStatus.BAD_REQUEST.value(),
                Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    // 3️⃣ JSON parse / request format errors
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(Exception ex) {

        ApiError error = new ApiError(
                "MALFORMED_REQUEST",
                "Invalid request payload",
                HttpStatus.BAD_REQUEST.value(),
                Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    // 4️⃣ Catch-all (LAST HANDLER)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnhandled(Exception ex) {

        log.error("Unhandled exception", ex);

        ApiError error = new ApiError(
                "INTERNAL_SERVER_ERROR",
                "Something went wrong. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
