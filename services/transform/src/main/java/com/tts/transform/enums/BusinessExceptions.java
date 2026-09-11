package com.tts.transform.enums;

import com.platform.web.exception.ErrorDefinition;

public enum BusinessExceptions implements ErrorDefinition {

    SYNTHESIS_ERROR("SYNTHESIS_ERROR", "BUS-5002", "Error while synthesizing text to audio."),
    STORAGE_ERROR("STORAGE_ERROR", "BUS-5003", "Error while storing the audio file(s)."),
    MAX_LENGTH_EXCEEDED("MAX_LENGTH_EXCEEDED", "BUS-5004", "Max characters length to convert to speech was exceeded."),
    DOCUMENT_EXTRACTION_ERROR("DOCUMENT_EXTRACTION_ERROR", "BUS-5005",
            "Error occured while extracting contents from the file."),
    USAGE_LIMIT_EXHAUSTED("USAGE_LIMIT_EXHAUSTED", "BUS-2001", "Your Monthly Limit Exhausted for this month."),

    INTERNAL_COMMUNICATION_ERROR("INTERNAL_COMMUNICATION_ERROR", "BUS-5001",
            "Error connecting to the requested service.");

    private String name;
    private String code;
    private String message;

    private BusinessExceptions(String name, String code, String message) {
        this.name = name;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getErrorName() {
        return name;
    }

    @Override
    public String getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMessage() {
        return message;
    }
}
