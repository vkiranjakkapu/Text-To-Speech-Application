package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;
import com.openai.models.responses.ResponseOutputMessage;
import com.openai.models.responses.ResponseOutputText;
import com.openai.services.blocking.ResponseService;
import com.tts.transform.properties.AzureOpenAiProperties;

class AzureAiServiceImpTest {

	private OpenAIClient client;

	private AzureOpenAiProperties properties;

	private AzureAiServiceImp service;

	@BeforeEach
	void setUp() {

		client = mock(OpenAIClient.class);
		properties = mock(AzureOpenAiProperties.class);

		when(properties.endpoint())
				.thenReturn("https://test.openai.azure.com");

		when(properties.apiKey())
				.thenReturn("test-api-key");

		when(properties.deployment())
				.thenReturn("test-deployment");

		try (MockedStatic<OpenAIOkHttpClient> mocked = mockStatic(OpenAIOkHttpClient.class)) {

			OpenAIOkHttpClient.Builder builder = mock(OpenAIOkHttpClient.Builder.class);

			mocked.when(OpenAIOkHttpClient::builder)
					.thenReturn(builder);

			when(builder.baseUrl("https://test.openai.azure.com"))
					.thenReturn(builder);

			when(builder.apiKey("test-api-key"))
					.thenReturn(builder);

			when(builder.azureUrlPathMode(
					com.openai.azure.AzureUrlPathMode.UNIFIED))
					.thenReturn(builder);

			when(builder.build())
					.thenReturn(client);

			service = new AzureAiServiceImp(properties);
		}
	}

	@Test
	void enhanceText_shouldReturnEnhancedText() {

		String result = mockResponse("Enhanced spoken text");

		assertEquals(
				result,
				service.enhanceText("Original text"));
	}

	@Test
	void resizeText_shouldReturnResizedText() {

		String result = mockResponse("Resized text");

		assertEquals(
				"Resized text",
				service.resizeText("Original text", 100));

		assertEquals("Resized text", result);
	}

	@Test
	void summariseText_shouldReturnSummarisedText() {

		String result = mockResponse("Summarised text");

		assertEquals(
				"Summarised text",
				service.summariseText("Long original text"));

		assertEquals("Summarised text", result);
	}

	private String mockResponse(String output) {

		Response response = mock(Response.class);

		ResponseService responses = mock(ResponseService.class);

		when(client.responses())
				.thenReturn(responses);

		when(responses.create(
				any(ResponseCreateParams.class)))
				.thenReturn(response);

		ResponseOutputText outputText = mock(ResponseOutputText.class);

		when(outputText.text())
				.thenReturn(output);

		ResponseOutputMessage.Content content = mock(ResponseOutputMessage.Content.class);

		when(content.outputText())
				.thenReturn(java.util.Optional.of(outputText));

		ResponseOutputMessage message = mock(ResponseOutputMessage.class);

		when(message.content())
				.thenReturn(List.of(content));

		ResponseOutputItem outputItem = mock(ResponseOutputItem.class);

		when(outputItem.message())
				.thenReturn(java.util.Optional.of(message));

		when(response.output())
				.thenReturn(List.of(outputItem));

		return output;
	}
}