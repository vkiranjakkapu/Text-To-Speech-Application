package com.tts.transform.services.imp;

import org.springframework.stereotype.Service;

import com.openai.azure.AzureUrlPathMode;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.ResponseCreateParams;
import com.tts.transform.properties.AzureOpenAiProperties;
import com.tts.transform.services.AzureAiService;

@Service
public class AzureAiServiceImp implements AzureAiService {

	private final OpenAIClient client;
	private final String deployment;

	public AzureAiServiceImp(AzureOpenAiProperties properties) {

		this.client = OpenAIOkHttpClient.builder()
				.baseUrl(properties.endpoint())
				.apiKey(properties.apiKey())
				.azureUrlPathMode(AzureUrlPathMode.UNIFIED)
				.build();

		this.deployment = properties.deployment();
	}

	@Override
	public String enhanceText(String text) {

		ResponseCreateParams createParams = ResponseCreateParams.builder()
				.model(deployment)
				.input("""
						Improve the following text for natural
						spoken delivery. Preserve its meaning.
						Do not add new information.

						Text:
						%s
						""".formatted(text))
				.build();

		return getResponse(createParams);
	}

	@Override
	public String resizeText(String text, long length) {

		ResponseCreateParams createParams = ResponseCreateParams.builder()
				.model(deployment)
				.input("""
						Resize the following text to %d characters length for natural
						spoken delivery. Preserve its meaning, but if not possible, 
						club and summarise wherever possible to reach the required length.
						Remove special characters wherever possible.
						Do not add new information.

						Text:
						%s
						"""
						.formatted(length, text))
				.build();

		return getResponse(createParams);
	}

	@Override
	public String summariseText(String text) {

		ResponseCreateParams createParams = ResponseCreateParams.builder()
				.model(deployment)
				.input("""
						Summarise the following text for natural
						spoken delivery. Preserve its meaning.
						Do not add new information.

						Text:
						%s
						""".formatted(text))
				.build();

		return getResponse(createParams);
	}

	private String getResponse(ResponseCreateParams createParams) {
		return client.responses()
				.create(createParams)
				.output()
				.stream()
				.flatMap(item -> item.message().stream())
				.flatMap(message -> message.content().stream())
				.flatMap(content -> content.outputText().stream())
				.map(outputText -> outputText.text())
				.collect(java.util.stream.Collectors.joining());
	}

}