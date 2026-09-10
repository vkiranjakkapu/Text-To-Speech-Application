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
            String voice) {

        SpeechConfig config = SpeechConfig.fromSubscription(
                properties.key(),
                properties.region());

        config.setSpeechSynthesisLanguage(language);
        config.setSpeechSynthesisVoiceName(voice);

        config.setSpeechSynthesisOutputFormat(
                SpeechSynthesisOutputFormat.Audio24Khz48KBitRateMonoMp3);

        try (SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, null)) {

            SpeechSynthesisResult result = synthesizer.SpeakText(text);

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                return result.getAudioData();
            }

            if (result.getReason() == ResultReason.Canceled) {

                SpeechSynthesisCancellationDetails details = SpeechSynthesisCancellationDetails.fromResult(result);

                throw new BusinessException(BusinessExceptions.SYNTHESIS_ERROR,
                        "Azure TTS cancelled: "
                                + details.getReason()
                                + " - "
                                + details.getErrorDetails());
            }

            throw new BusinessException(BusinessExceptions.SYNTHESIS_ERROR,
                    "Azure TTS failed: " + result.getReason());
        }
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
                        .build())
                .toList();
    }
}