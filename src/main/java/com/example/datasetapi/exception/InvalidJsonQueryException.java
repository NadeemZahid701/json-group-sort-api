package com.example.datasetapi.exception;

public class InvalidJsonQueryException extends RuntimeException {
    public InvalidJsonQueryException(String message) {
        super(message);
    }
}
