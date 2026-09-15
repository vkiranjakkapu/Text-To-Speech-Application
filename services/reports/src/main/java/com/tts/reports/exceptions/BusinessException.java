package com.tts.reports.exceptions;

import org.springframework.http.HttpStatus;

import com.platform.web.exception.ErrorDefinition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {

    private ErrorDefinition definition;
    private HttpStatus status;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(ErrorDefinition definition, Throwable cause) {
        super(cause);
        this.definition = definition;
    }

    public BusinessException(ErrorDefinition definition, String message) {
        super(message);
        this.definition = definition;
    }

    public BusinessException(ErrorDefinition definition, HttpStatus status) {
        this.definition = definition;
        this.status = status;
    }

    public BusinessException(ErrorDefinition definition, String message, HttpStatus status) {
        super(message);
        this.definition = definition;
        this.status = status;
    }

    public BusinessException(ErrorDefinition definition, String message, Throwable cause) {
        super(message, cause);
        this.definition = definition;
    }

}
