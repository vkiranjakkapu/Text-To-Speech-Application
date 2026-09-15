package com.tts.transform.exceptions;

import com.platform.web.exception.ErrorDefinition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SecurityException extends RuntimeException {

    private ErrorDefinition definition;

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityException(ErrorDefinition definition) {
        this.definition = definition;
    }

    public SecurityException(ErrorDefinition definition, String message, Throwable cause) {
        super(message, cause);
        this.definition = definition;
    }

}
