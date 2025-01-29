package com.tenpo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTooManyRequestsException(TooManyRequestsException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("Too Many Requests")
                .message(ex.getMessage())
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validation failed")
                .fieldErrors(fieldErrors)
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleServerWebInputException(ServerWebInputException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Invalid input: " + ex.getReason())
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }
}