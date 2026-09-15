package com.tts.transform.dto;

import com.platform.web.model.ErrorResponse;

import lombok.Builder;

@Builder
public record DocumentSpeechResponseDto(ErrorResponse error, String suggestion, String text) {

}
