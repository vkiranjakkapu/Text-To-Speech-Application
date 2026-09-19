package com.tts.transform.dto;

import lombok.Builder;

@Builder
public record SupportedDocumentType(
		String mimeType,
		String name,
		String extension) {
}
