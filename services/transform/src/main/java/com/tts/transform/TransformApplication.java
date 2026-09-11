package com.tts.transform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.tts.transform.properties.AzureSpeechProperties;
import com.tts.transform.properties.AzureStorageProperties;
import com.tts.transform.properties.DefaultProperties;

@SpringBootApplication
@EnableConfigurationProperties({ DefaultProperties.class, AzureSpeechProperties.class, AzureStorageProperties.class })
public class TransformApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransformApplication.class, args);
	}

}
