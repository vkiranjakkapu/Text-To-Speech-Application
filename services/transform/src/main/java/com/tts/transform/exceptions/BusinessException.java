package com.tts.transform.exceptions;

import com.platform.web.exception.ErrorDefinition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {

    private ErrorDefinition definition;

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

    public BusinessException(ErrorDefinition definition, String message, Throwable cause) {
        super(message, cause);
        this.definition = definition;
    }

}
