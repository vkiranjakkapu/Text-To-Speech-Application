package com.tts.transform.dto;

import jakarta.validation.constraints.NotEmpty;

public record SynthesizeRequest(
		@NotEmpty String text,
		@NotEmpty String language,
		@NotEmpty String voice) {
}