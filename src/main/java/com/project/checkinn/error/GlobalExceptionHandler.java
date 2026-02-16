package com.project.checkinn.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        int status = ex.getStatusCode().value();

        ErrorResponseDto body = new ErrorResponseDto(
                status,
                ex.getReason(),
                req.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );
        return ResponseEntity.status(status).body(body);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        List<FieldErrorDto> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> new FieldErrorDto(e.getField(), e.getDefaultMessage()))
                .toList();

        ErrorResponseDto body = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                req.getRequestURI(),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAny(Exception ex, HttpServletRequest req) {

        ErrorResponseDto body = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error",
                req.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
    }
