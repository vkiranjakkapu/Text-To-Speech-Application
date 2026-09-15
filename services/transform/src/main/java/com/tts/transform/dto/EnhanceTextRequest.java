package com.tts.transform.dto;

import com.tts.transform.enums.EnhancementType;

import jakarta.validation.constraints.NotBlank;

public record EnhanceTextRequest(@NotBlank String text, EnhancementType type, long length) {
}