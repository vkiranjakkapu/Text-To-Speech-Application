package com.tts.transform.dto;

import com.tts.transform.enums.SynthesisType;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record SynthesizeRequest(
		@NotEmpty String text,
		@NotEmpty String language,
		@NotEmpty String voice,
		SynthesisType type,
		String style,
		String rate,
		String pitch,
		String volume) {
}