package com.tts.transform.controllers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tts.transform.dto.SynthesizeRequest;
import com.tts.transform.properties.DefaultProperties;
import com.tts.transform.services.SpeechService;

import jakarta.servlet.ServletException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.dto.VoiceDto;
import com.tts.transform.services.TtsProvider;

@ExtendWith(MockitoExtension.class)
class SpeechControllerTest {

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	@Mock
	private DefaultProperties properties;

	@Mock
	private DefaultProperties.IncomingRequest requestProperties;

	@Mock
	private SpeechService speechService;

	@Mock
	private TtsProvider ttsProvider;

	@InjectMocks
	private SpeechController speechController;

	private SynthesizeRequest request;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();

		mockMvc = MockMvcBuilders
				.standaloneSetup(speechController)
				.build();

		request = SynthesizeRequest.builder()
				.text("Hello, welcome to the application.")
				.language("en-US")
				.voice("en-US-JennyNeural")
				.style("general")
				.rate("0")
				.pitch("0")
				.volume("0")
				.build();
	}

	@Test
	void synthesize_shouldReturnAudio() throws Exception {
		byte[] audio = "audio".getBytes();

		when(properties.getRequest()).thenReturn(requestProperties);
		when(requestProperties.getMaxTextLength()).thenReturn(200L);
		when(speechService.synthesize(any(SynthesizeRequest.class)))
				.thenReturn(audio);

		mockMvc.perform(post("/speech/api/v1/synthesize")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.valueOf("audio/mpeg")))
				.andExpect(content().bytes(audio));

		verify(speechService).synthesize(any(SynthesizeRequest.class));
	}

	@Test
	void synthesize_shouldRejectEmptyText() throws Exception {
		SynthesizeRequest invalidRequest = SynthesizeRequest.builder()
				.text("")
				.language("en-US")
				.voice("en-US-JennyNeural")
				.build();

		mockMvc.perform(post("/speech/api/v1/synthesize")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void synthesize_shouldRejectMissingText() throws Exception {
		String json = """
				{
				    "language": "en-US",
				    "voice": "en-US-JennyNeural"
				}
				""";

		mockMvc.perform(post("/speech/api/v1/synthesize")
				.contentType(APPLICATION_JSON)
				.content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	void synthesize_shouldRejectUnsupportedContentType() throws Exception {
		mockMvc.perform(post("/speech/api/v1/synthesize")
				.contentType(MediaType.TEXT_PLAIN)
				.content("Hello world"))
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	void synthesize_shouldRejectMalformedJson() throws Exception {
		mockMvc.perform(post("/speech/api/v1/synthesize")
				.contentType(APPLICATION_JSON)
				.content("{invalid-json"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void synthesize_shouldReturnInternalServerErrorWhenServiceFails()
			throws Exception {

		when(properties.getRequest()).thenReturn(requestProperties);
		when(requestProperties.getMaxTextLength()).thenReturn(200L);

		when(speechService.synthesize(any(SynthesizeRequest.class)))
				.thenThrow(new RuntimeException("TTS provider failure"));

		assertThrows(
				ServletException.class,
				() -> mockMvc.perform(post("/speech/api/v1/synthesize")
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))));
	}

	@Test
	void getVoices_shouldReturnVoices() {

		List<VoiceDto> voices = List.of(
				VoiceDto.builder()
						.id("en-US-JennyNeural")
						.name("Jenny")
						.language("en-US")
						.languageName("English (United States)")
						.gender("Female")
						.styles(List.of("general"))
						.build());

		when(ttsProvider.getVoices()).thenReturn(voices);

		ResponseEntity<ApiResponseDto> response = speechController.getVoices();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(voices, response.getBody().data());

		verify(ttsProvider).getVoices();
	}

	@Test
	void downloadSpeech_shouldReturnAudio() {

		UUID speechId = UUID.randomUUID();
		byte[] audio = "audio".getBytes();

		when(speechService.download(speechId)).thenReturn(audio);

		ResponseEntity<byte[]> response = speechController.downloadSpeech(speechId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertArrayEquals(audio, response.getBody());

		verify(speechService).download(speechId);
	}
}