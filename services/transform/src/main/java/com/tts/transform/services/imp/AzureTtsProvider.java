package com.tts.transform.services.imp;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.tts.transform.dto.VoiceDto;
import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.properties.AzureSpeechProperties;
import com.tts.transform.services.TtsProvider;

@Service
public class AzureTtsProvider implements TtsProvider {

	private final AzureSpeechProperties properties;
	private final RestClient restClient;

	public AzureTtsProvider(AzureSpeechProperties properties, RestClient restClient) {
		this.properties = properties;
		this.restClient = restClient;
	}

	@Override
	public byte[] synthesize(
			String text,
			String language,
			String voice,
			String style,
			String rate,
			String pitch,
			String volume) {

		SpeechConfig config = SpeechConfig.fromSubscription(
				properties.key(),
				properties.region());

		config.setSpeechSynthesisLanguage(language);
		config.setSpeechSynthesisVoiceName(voice);

		config.setSpeechSynthesisOutputFormat(
				SpeechSynthesisOutputFormat.Audio24Khz48KBitRateMonoMp3);

		try (SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, null)) {

			String ssml = buildSsml(
					text,
					language,
					voice,
					style,
					rate,
					pitch,
					volume);

			SpeechSynthesisResult result = synthesizer.SpeakSsml(ssml);

			if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
				return result.getAudioData();
			}

			if (result.getReason() == ResultReason.Canceled) {

				SpeechSynthesisCancellationDetails details = SpeechSynthesisCancellationDetails.fromResult(result);

				throw new BusinessException(
						BusinessExceptions.SYNTHESIS_ERROR,
						"Azure TTS cancelled: "
								+ details.getReason()
								+ " - "
								+ details.getErrorDetails());
			}

			throw new BusinessException(
					BusinessExceptions.SYNTHESIS_ERROR,
					"Azure TTS failed: " + result.getReason());
		}
	}

	private String buildSsml(
			String text,
			String language,
			String voice,
			String style,
			String rate,
			String pitch,
			String volume) {

		StringBuilder ssml = new StringBuilder();

		ssml.append("""
				<speak version="1.0"
				       xmlns="http://www.w3.org/2001/10/synthesis"
				       xmlns:mstts="https://www.w3.org/2001/mstts"
				       xml:lang="%s">
				""".formatted(escapeXml(language)));

		ssml.append("<voice name=\"")
				.append(escapeXml(voice))
				.append("\">");

		boolean hasProsody = isPresent(rate)
				|| isPresent(pitch)
				|| isPresent(volume);

		if (isPresent(style)) {
			ssml.append("<mstts:express-as style=\"")
					.append(escapeXml(style))
					.append("\">");
		}

		if (hasProsody) {
			ssml.append("<prosody");

			if (isPresent(rate)) {
				ssml.append(" rate=\"")
						.append(escapeXml(rate))
						.append("\"");
			}

			if (isPresent(pitch)) {
				ssml.append(" pitch=\"")
						.append(escapeXml(pitch))
						.append("\"");
			}

			if (isPresent(volume)) {
				ssml.append(" volume=\"")
						.append(escapeXml(volume))
						.append("\"");
			}

			ssml.append(">");
		}

		ssml.append(escapeXml(text));

		if (hasProsody) {
			ssml.append("</prosody>");
		}

		if (isPresent(style)) {
			ssml.append("</mstts:express-as>");
		}

		ssml.append("</voice></speak>");

		return ssml.toString();
	}

	private boolean isPresent(String value) {
		return value != null && !value.isBlank();
	}

	private String escapeXml(String value) {
		return value
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

	@Override
	public List<VoiceDto> getVoices() {

		List<Map<String, Object>> body = restClient.get()
				.uri("https://" + properties.region()
						+ ".tts.speech.microsoft.com/cognitiveservices/voices/list")
				.header("Ocp-Apim-Subscription-Key", properties.key())
				.retrieve()
				.body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});

		return body.stream()
				.map(voice -> VoiceDto.builder()
						.id((String) voice.get("ShortName"))
						.name((String) voice.get("DisplayName"))
						.language((String) voice.get("Locale"))
						.languageName((String) voice.get("LocaleName"))
						.gender((String) voice.get("Gender"))
						.styles(
								voice.get("StyleList") instanceof List<?> list
										? list.stream()
												.filter(String.class::isInstance)
												.map(String.class::cast)
												.toList()
										: List.of())
						.build())
				.toList();
	}
}