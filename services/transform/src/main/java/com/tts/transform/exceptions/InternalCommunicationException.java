package com.tts.transform.exceptions;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternalCommunicationException extends RuntimeException {

    private HttpStatusCode status;

    public InternalCommunicationException(String message) {
        super(message);
    }

    public InternalCommunicationException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }

}