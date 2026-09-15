package com.tts.reports.dto;

import java.time.LocalDateTime;

import com.tts.reports.enums.ResponseStatus;

import lombok.Builder;

@Builder
public record ApiResponseDto(ResponseStatus status, Object data, LocalDateTime timestamp) {

}
