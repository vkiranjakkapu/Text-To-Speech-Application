package com.tts.transform.dto;

import java.time.LocalDateTime;

import com.tts.transform.enums.ResponseStatus;

import lombok.Builder;

@Builder
public record ApiResponseDto(
        ResponseStatus status,
        Object data,
        LocalDateTime timestamp) {

    public ApiResponseDto {
        if (status == null) {
            status = ResponseStatus.SUCCESS;
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
