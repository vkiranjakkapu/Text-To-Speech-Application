package com.tts.transform.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "azure.openai")
public record AzureOpenAiProperties(String endpoint,
        String apiKey,
        String deployment) {

}
