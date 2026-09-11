package com.tts.transform.dto;

import jakarta.validation.constraints.NotEmpty;

public record SynthesizeRequest(
		@NotEmpty String text,
		@NotEmpty String language,
		@NotEmpty String voice,
		String style,
		String rate,
		String pitch,
		String volume) {
}