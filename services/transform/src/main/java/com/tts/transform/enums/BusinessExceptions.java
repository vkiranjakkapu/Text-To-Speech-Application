package com.tts.transform.enums;

import com.platform.web.exception.ErrorDefinition;

public enum BusinessExceptions implements ErrorDefinition {

    SYNTHESIS_ERROR("SYNTHESIS_ERROR", "BUS-5002", "Error while synthesizing text to audio."),
    STORAGE_ERROR("STORAGE_ERROR", "BUS-5003", "Error while storing the audio file(s)."),
    MIN_LENGTH_REQUIRED("MIN_LENGTH_REQUIRED", "BUS-4002", "Min characters length should be provided to text synthesis.."),
    MAX_LENGTH_EXCEEDED("MAX_LENGTH_EXCEEDED", "BUS-4003", "Max characters length to convert to speech was exceeded."),
    DOCUMENT_EXTRACTION_ERROR("DOCUMENT_EXTRACTION_ERROR", "BUS-5005",
            "Error occured while extracting contents from the file."),
    USAGE_LIMIT_EXHAUSTED("USAGE_LIMIT_EXHAUSTED", "BUS-2001", "Your Monthly Limit Exhausted for this month."),
    LIMIT_EXCEEDED("LIMIT_EXCEEDED", "BUS-2001", "Your input text is exceeding the remainig limit"),

    INTERNAL_COMMUNICATION_ERROR("INTERNAL_COMMUNICATION_ERROR", "BUS-5001",
            "Error connecting to the requested service.");

    private String errorName;
    private String errorCode;
    private String errorMessage;

    private BusinessExceptions(String name, String code, String message) {
        this.errorName = name;
        this.errorCode = code;
        this.errorMessage = message;
    }

    @Override
    public String getErrorName() {
        return errorName;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
