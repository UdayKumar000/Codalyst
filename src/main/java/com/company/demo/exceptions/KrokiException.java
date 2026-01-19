package com.company.demo.exceptions;

public class KrokiException extends RuntimeException {

    private final int statusCode;
    private final String krokiError;

    public KrokiException(int statusCode, String krokiError) {
        super("Kroki error: " + statusCode);
        this.statusCode = statusCode;
        this.krokiError = krokiError;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getKrokiError() {
        return krokiError;
    }
}
