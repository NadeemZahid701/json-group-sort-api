package com.example.datasetapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({HttpMessageNotReadableException.class, InvalidJsonQueryException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidJson(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid JSON request body.", request.getRequestURI(), ex);
    }

    @ExceptionHandler({InvalidDatasetNameException.class, InvalidQueryParameterException.class, UnsupportedJsonValueException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error.", request.getRequestURI(), ex);
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message, String path, Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return ResponseEntity.status(status).body(body);
    }
}
