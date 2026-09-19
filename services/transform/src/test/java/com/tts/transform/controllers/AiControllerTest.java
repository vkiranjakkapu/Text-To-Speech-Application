package com.tts.transform.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.dto.EnhanceTextRequest;
import com.tts.transform.enums.EnhancementType;
import com.tts.transform.exceptions.ValidationException;
import com.tts.transform.services.AzureAiService;

class AiControllerTest {

	private AzureAiService aiService;

	private AiController controller;

	@BeforeEach
	void setUp() {

		aiService = mock(AzureAiService.class);

		controller = new AiController(aiService);
	}

	@Test
	void enhance_shouldEnhanceTextByDefault() {

		EnhanceTextRequest request = new EnhanceTextRequest(
				"Original text",
				null,
				null);

		when(aiService.enhanceText("Original text"))
				.thenReturn("Enhanced text");

		ResponseEntity<?> response = controller.enhance(request);

		ApiResponseDto body = (ApiResponseDto) response.getBody();

		assertEquals("Enhanced text", body.data());

		verify(aiService).enhanceText("Original text");
	}

	@Test
	void enhance_shouldEnhanceTextWhenTypeIsEnhance() {

		EnhanceTextRequest request = new EnhanceTextRequest(
				"Original text",
				EnhancementType.ENHANCE,
				null);

		when(aiService.enhanceText("Original text"))
				.thenReturn("Enhanced text");

		ResponseEntity<?> response = controller.enhance(request);
		ApiResponseDto body = (ApiResponseDto) response.getBody();

		assertEquals("Enhanced text", body.data());

		verify(aiService).enhanceText("Original text");
	}

	@Test
	void enhance_shouldReduceTextWhenTypeIsReduce() {

		EnhanceTextRequest request = new EnhanceTextRequest(
				"Original text",
				EnhancementType.REDUCE,
				100L);

		when(aiService.resizeText("Original text", 100L))
				.thenReturn("Reduced text");

		ResponseEntity<?> response = controller.enhance(request);

		ApiResponseDto body = (ApiResponseDto) response.getBody();
		assertEquals("Reduced text", body.data());

		verify(aiService).resizeText("Original text", 100L);
	}

	@Test
	void enhance_shouldThrowValidationExceptionWhenReduceLengthIsMissing() {

		EnhanceTextRequest request = new EnhanceTextRequest(
				"Original text",
				EnhancementType.REDUCE,
				null);

		assertThrows(
				ValidationException.class,
				() -> controller.enhance(request));

		verifyNoInteractions(aiService);
	}

	@Test
	void enhance_shouldSummariseTextWhenTypeIsSummarise() {

		EnhanceTextRequest request = new EnhanceTextRequest(
				"Original text",
				EnhancementType.SUMMARISE,
				null);

		when(aiService.summariseText("Original text"))
				.thenReturn("Summarised text");

		ResponseEntity<?> response = controller.enhance(request);

		ApiResponseDto body = (ApiResponseDto) response.getBody();

		assertEquals("Summarised text", body.data());

		verify(aiService).summariseText("Original text");
	}
}