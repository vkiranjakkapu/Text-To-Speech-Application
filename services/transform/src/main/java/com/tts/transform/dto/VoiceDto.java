package com.tts.transform.dto;

import lombok.Builder;

@Builder
public record VoiceDto(String id,
		String name,
		String language,
		String languageName,
		String gender) {
}
