// src/main/java/com/alpopan/taskmaster/exception/RestExceptionHandler.java
package com.alpopan.taskmaster.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
        ApiError err = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(err.getStatus()).body(err);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ApiError err = new ApiError(HttpStatus.BAD_REQUEST, "Validation failed");
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            err.addFieldError(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.status(err.getStatus()).body(err);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiError> handleAll(Exception ex) {
        ApiError err = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        return ResponseEntity.status(err.getStatus()).body(err);
    }
}
