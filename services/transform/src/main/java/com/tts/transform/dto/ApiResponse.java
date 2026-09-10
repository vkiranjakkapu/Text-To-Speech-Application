package com.tts.transform.dto;

import java.time.LocalDateTime;

import com.tts.transform.enums.ResponseStatus;

import lombok.Builder;

@Builder
public record ApiResponse(
        ResponseStatus status,
        Object body,
        LocalDateTime timestamp) {

    public ApiResponse {
        if (status == null) {
            status = ResponseStatus.SUCCESS;
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
