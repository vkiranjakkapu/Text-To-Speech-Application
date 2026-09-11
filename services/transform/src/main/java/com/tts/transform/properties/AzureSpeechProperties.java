package com.tts.transform.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "azure.speech")
public record AzureSpeechProperties(String key,
		String region) {

}
