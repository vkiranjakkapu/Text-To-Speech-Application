package com.tts.transform.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.platform.web.exception.ErrorDefinition;
import com.platform.web.exception.SecurityExceptions;
import com.platform.web.exception.WebExceptions;
import com.platform.web.model.ErrorResponse;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.exceptions.SecurityException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e) {
        Optional.ofNullable(e.getCause()).ifPresent(er -> er.printStackTrace());
        ErrorDefinition definition = Optional.ofNullable(e.getDefinition()).orElse(SecurityExceptions.FORBIDDEN_ACCESS);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(definition,
                        Optional.ofNullable(e.getMessage()).orElse(definition.getErrorMessage())));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        Optional.ofNullable(e.getCause()).ifPresent(er -> er.printStackTrace());
        ErrorDefinition definition = Optional.ofNullable(e.getDefinition()).orElse(WebExceptions.APPLICATION_ERROR);
        return ResponseEntity.status(Optional.ofNullable(e.getStatus()).orElse(HttpStatus.BAD_REQUEST))
                .body(new ErrorResponse(definition,
                        Optional.ofNullable(e.getMessage()).orElse(definition.getErrorMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedExceptions(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(WebExceptions.APPLICATION_ERROR, e.getMessage()));
    }

}
