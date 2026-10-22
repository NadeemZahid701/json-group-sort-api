package com.example.datasetapi.exception;

public class InvalidDatasetNameException extends RuntimeException {
    public InvalidDatasetNameException(String message) {
        super(message);
    }
}
