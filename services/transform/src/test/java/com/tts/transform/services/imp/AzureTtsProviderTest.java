package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.tts.transform.dto.VoiceDto;
import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.properties.AzureSpeechProperties;

class AzureTtsProviderTest {

	private AzureSpeechProperties properties;

	private RestClient restClient;

	private AzureTtsProvider provider;

	@BeforeEach
	void setUp() {
		properties = mock(AzureSpeechProperties.class);
		restClient = mock(RestClient.class);

		when(properties.key()).thenReturn("test-key");
		when(properties.region()).thenReturn("eastus");

		provider = new AzureTtsProvider(properties, restClient);
	}

	@Test
	void synthesize_shouldReturnAudioWhenSynthesisCompletes() {

		byte[] audio = "audio-data".getBytes();

		SpeechConfig config = mock(SpeechConfig.class);
		SpeechSynthesisResult result = mock(SpeechSynthesisResult.class);

		when(result.getReason())
				.thenReturn(ResultReason.SynthesizingAudioCompleted);

		when(result.getAudioData())
				.thenReturn(audio);

		try (MockedStatic<SpeechConfig> speechConfigMock = mockStatic(SpeechConfig.class);

				MockedConstruction<SpeechSynthesizer> synthesizerMock = mockConstruction(
						SpeechSynthesizer.class,
						(synthesizer, context) -> {
							when(synthesizer.SpeakSsml(any(String.class)))
									.thenReturn(result);
						})) {

			speechConfigMock
					.when(() -> SpeechConfig.fromSubscription(
							"test-key",
							"eastus"))
					.thenReturn(config);

			byte[] actual = provider.synthesize(
					"Hello",
					"en-US",
					"en-US-JennyNeural",
					"general",
					"0",
					"0",
					"0");

			assertArrayEquals(audio, actual);

			verify(config).setSpeechSynthesisLanguage("en-US");
			verify(config).setSpeechSynthesisVoiceName("en-US-JennyNeural");

			SpeechSynthesizer synthesizer = synthesizerMock.constructed().get(0);

			verify(synthesizer).SpeakSsml(any(String.class));
		}
	}

	@Test
	void synthesize_shouldThrowBusinessExceptionWhenSynthesisIsCancelled() {

		SpeechConfig config = mock(SpeechConfig.class);
		SpeechSynthesisResult result = mock(SpeechSynthesisResult.class);
		SpeechSynthesisCancellationDetails details = mock(SpeechSynthesisCancellationDetails.class);

		when(result.getReason())
				.thenReturn(ResultReason.Canceled);

		when(details.getReason())
				.thenReturn(CancellationReason.Error);

		when(details.getErrorDetails())
				.thenReturn("Invalid voice");

		try (MockedStatic<SpeechConfig> speechConfigMock = mockStatic(SpeechConfig.class);

				MockedStatic<SpeechSynthesisCancellationDetails> cancellationMock = mockStatic(
						SpeechSynthesisCancellationDetails.class);

				MockedConstruction<SpeechSynthesizer> synthesizerMock = mockConstruction(
						SpeechSynthesizer.class,
						(synthesizer, context) -> {
							when(synthesizer.SpeakSsml(any(String.class)))
									.thenReturn(result);
						})) {

			speechConfigMock
					.when(() -> SpeechConfig.fromSubscription(
							"test-key",
							"eastus"))
					.thenReturn(config);

			cancellationMock
					.when(() -> SpeechSynthesisCancellationDetails.fromResult(result))
					.thenReturn(details);

			BusinessException exception = assertThrows(
					BusinessException.class,
					() -> provider.synthesize(
							"Hello",
							"en-US",
							"en-US-JennyNeural",
							null,
							null,
							null,
							null));

			assertEquals(
					BusinessExceptions.SYNTHESIS_ERROR,
					exception.getDefinition());

			SpeechSynthesizer synthesizer = synthesizerMock.constructed().get(0);

			verify(synthesizer).SpeakSsml(any(String.class));
		}
	}

	@Test
	void synthesize_shouldThrowBusinessExceptionWhenSynthesisFails() {

		SpeechConfig config = mock(SpeechConfig.class);
		SpeechSynthesisResult result = mock(SpeechSynthesisResult.class);

		when(result.getReason())
				.thenReturn(ResultReason.NoMatch);

		try (MockedStatic<SpeechConfig> speechConfigMock = mockStatic(SpeechConfig.class);

				MockedConstruction<SpeechSynthesizer> synthesizerMock = mockConstruction(
						SpeechSynthesizer.class,
						(synthesizer, context) -> {
							when(synthesizer.SpeakSsml(any(String.class)))
									.thenReturn(result);
						})) {

			speechConfigMock
					.when(() -> SpeechConfig.fromSubscription(
							"test-key",
							"eastus"))
					.thenReturn(config);

			BusinessException exception = assertThrows(
					BusinessException.class,
					() -> provider.synthesize(
							"Hello",
							"en-US",
							"en-US-JennyNeural",
							null,
							null,
							null,
							null));

			assertEquals(
					BusinessExceptions.SYNTHESIS_ERROR,
					exception.getDefinition());

			SpeechSynthesizer synthesizer = synthesizerMock.constructed().get(0);

			verify(synthesizer).SpeakSsml(any(String.class));
		}
	}

	@Test
	void synthesize_shouldBuildSsmlWithEscapedValuesAndProsody() {

		SpeechConfig config = mock(SpeechConfig.class);
		SpeechSynthesisResult result = mock(SpeechSynthesisResult.class);

		when(result.getReason())
				.thenReturn(ResultReason.SynthesizingAudioCompleted);

		when(result.getAudioData())
				.thenReturn(new byte[] { 1, 2, 3 });

		try (MockedStatic<SpeechConfig> speechConfigMock = mockStatic(SpeechConfig.class);

				MockedConstruction<SpeechSynthesizer> synthesizerMock = mockConstruction(
						SpeechSynthesizer.class,
						(synthesizer, context) -> {
							when(synthesizer.SpeakSsml(any(String.class)))
									.thenReturn(result);
						})) {

			speechConfigMock
					.when(() -> SpeechConfig.fromSubscription(
							"test-key",
							"eastus"))
					.thenReturn(config);

			provider.synthesize(
					"Hello <World> & \"Test\"",
					"en-US",
					"en-US-JennyNeural",
					"cheerful",
					"10%",
					"+5Hz",
					"20");

			SpeechSynthesizer synthesizer = synthesizerMock.constructed().get(0);

			verify(synthesizer).SpeakSsml(
					eq("""
							<speak version="1.0"
							       xmlns="http://www.w3.org/2001/10/synthesis"
							       xmlns:mstts="https://www.w3.org/2001/mstts"
							       xml:lang="en-US">
							<voice name="en-US-JennyNeural"><mstts:express-as style="cheerful"><prosody rate="10%" pitch="+5Hz" volume="20">Hello &lt;World&gt; &amp; &quot;Test&quot;</prosody></mstts:express-as></voice></speak>"""));
		}
	}

	@Test
	void synthesize_shouldBuildSsmlWithoutOptionalElementsWhenValuesAreBlank() {

		SpeechConfig config = mock(SpeechConfig.class);
		SpeechSynthesisResult result = mock(SpeechSynthesisResult.class);

		when(result.getReason())
				.thenReturn(ResultReason.SynthesizingAudioCompleted);

		when(result.getAudioData())
				.thenReturn(new byte[] { 1 });

		try (MockedStatic<SpeechConfig> speechConfigMock = mockStatic(SpeechConfig.class);

				MockedConstruction<SpeechSynthesizer> synthesizerMock = mockConstruction(
						SpeechSynthesizer.class,
						(synthesizer, context) -> {
							when(synthesizer.SpeakSsml(any(String.class)))
									.thenReturn(result);
						})) {

			speechConfigMock
					.when(() -> SpeechConfig.fromSubscription(
							"test-key",
							"eastus"))
					.thenReturn(config);

			provider.synthesize(
					"Hello",
					"en-US",
					"en-US-JennyNeural",
					null,
					null,
					null,
					null);

			SpeechSynthesizer synthesizer = synthesizerMock.constructed().get(0);

			verify(synthesizer).SpeakSsml(
					eq("""
							<speak version="1.0"
							       xmlns="http://www.w3.org/2001/10/synthesis"
							       xmlns:mstts="https://www.w3.org/2001/mstts"
							       xml:lang="en-US">
							<voice name="en-US-JennyNeural">Hello</voice></speak>"""));
		}
	}

	@Test
	void getVoices_shouldReturnMappedVoices() {

		List<Map<String, Object>> response = List.of(
				Map.of(
						"ShortName", "en-US-JennyNeural",
						"DisplayName", "Jenny",
						"Locale", "en-US",
						"LocaleName", "English (United States)",
						"Gender", "Female",
						"StyleList", List.of("general", "cheerful")));

		@SuppressWarnings("rawtypes")
		RequestHeadersUriSpec requestUriSpec = mock(RestClient.RequestHeadersUriSpec.class);

		@SuppressWarnings("rawtypes")
		RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);

		ResponseSpec responseSpec = mock(ResponseSpec.class);

		doReturn(requestUriSpec)
				.when(restClient)
				.get();

		doReturn(requestHeadersSpec)
				.when(requestUriSpec)
				.uri("https://eastus.tts.speech.microsoft.com/cognitiveservices/voices/list");

		doReturn(requestHeadersSpec)
				.when(requestHeadersSpec)
				.header(
						"Ocp-Apim-Subscription-Key",
						"test-key");

		doReturn(responseSpec)
				.when(requestHeadersSpec)
				.retrieve();

		doReturn(response)
				.when(responseSpec)
				.body(
						ArgumentMatchers
								.<ParameterizedTypeReference<List<Map<String, Object>>>>any());

		List<VoiceDto> voices = provider.getVoices();

		assertEquals(1, voices.size());

		VoiceDto voice = voices.get(0);

		assertEquals("en-US-JennyNeural", voice.id());
		assertEquals("Jenny", voice.name());
		assertEquals("en-US", voice.language());
		assertEquals("English (United States)", voice.languageName());
		assertEquals("Female", voice.gender());
		assertEquals(
				List.of("general", "cheerful"),
				voice.styles());
	}

	@Test
	void getVoices_shouldReturnEmptyStylesWhenStyleListIsMissing() {

		@SuppressWarnings("rawtypes")
		RequestHeadersUriSpec requestUriSpec = mock(RestClient.RequestHeadersUriSpec.class);

		@SuppressWarnings("rawtypes")
		RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);

		ResponseSpec responseSpec = mock(ResponseSpec.class);

		List<Map<String, Object>> response = List.of(
				Map.of(
						"ShortName", "en-US-JennyNeural",
						"DisplayName", "Jenny",
						"Locale", "en-US",
						"LocaleName", "English (United States)",
						"Gender", "Female"));

		doReturn(requestUriSpec)
				.when(restClient)
				.get();

		doReturn(requestHeadersSpec)
				.when(requestUriSpec)
				.uri("https://eastus.tts.speech.microsoft.com/cognitiveservices/voices/list");

		doReturn(requestHeadersSpec)
				.when(requestHeadersSpec)
				.header(
						"Ocp-Apim-Subscription-Key",
						"test-key");

		doReturn(responseSpec)
				.when(requestHeadersSpec)
				.retrieve();

		doReturn(response)
				.when(responseSpec)
				.body(
						ArgumentMatchers
								.<ParameterizedTypeReference<List<Map<String, Object>>>>any());

		List<VoiceDto> voices = provider.getVoices();

		assertEquals(1, voices.size());
		assertEquals(List.of(), voices.get(0).styles());
	}
}