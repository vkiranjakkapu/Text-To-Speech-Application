package com.tts.transform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TransformApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransformApplication.class, args);
	}

}
