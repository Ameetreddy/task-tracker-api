package com.task.taskmanagement.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(
            DuplicateResourceException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> details = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                details.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                details
        );
    }
    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            Map<String, String> details) {

        ApiErrorResponse body = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );

        return ResponseEntity.status(status).body(body);
    }





}
