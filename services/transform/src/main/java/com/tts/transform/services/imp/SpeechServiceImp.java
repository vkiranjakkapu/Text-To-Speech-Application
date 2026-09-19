package com.tts.transform.services.imp;

import java.time.YearMonth;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tts.transform.dto.SynthesizeRequest;
import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.models.SpeechHistory;
import com.tts.transform.models.UsageMetrics;
import com.tts.transform.properties.DefaultProperties;
import com.tts.transform.services.AzureBlobStorageService;
import com.tts.transform.services.CurrentUserService;
import com.tts.transform.services.SpeechHistoryService;
import com.tts.transform.services.SpeechService;
import com.tts.transform.services.TtsProvider;
import com.tts.transform.services.UsageMetricsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpeechServiceImp implements SpeechService {

	private final TtsProvider ttsProvider;
	private final SpeechHistoryService historyService;
	private final UsageMetricsService metricsService;
	private final AzureBlobStorageService storageService;
	private final CurrentUserService currentUser;
	private final DefaultProperties properties;

	@Override
	public byte[] synthesize(SynthesizeRequest request) {

		UsageMetrics utilization = metricsService.getUtilizationByMonth(YearMonth.now());

		long remaining = utilization.getMaxLimit() - utilization.getUtilized();

		if (remaining <= 0) {
			String message = "Your Monthly Limit Exhausted.";

			if (Math.abs(remaining) > 25) {
				message += " You Have Already Exceeded Your Monthly Limit By "
						+ Math.abs(remaining) + " Characters";
			}

			throw new BusinessException(
					BusinessExceptions.USAGE_LIMIT_EXHAUSTED,
					message,
					HttpStatus.TOO_MANY_REQUESTS);
		}

		double maxOverdraftPercentage = properties.getLimits().getMaxOverdraftLimit();

		long maxAllowedLength = Math.round(
				remaining * (1 + maxOverdraftPercentage / 100.0));

		if (request.text().length() > maxAllowedLength) {
			throw new BusinessException(
					BusinessExceptions.LIMIT_EXCEEDED,
					"Your input text exceeds the available monthly limit including the allowed "
							+ maxOverdraftPercentage + "% overdraft.",
					HttpStatus.CONTENT_TOO_LARGE);
		}

		byte[] audio = ttsProvider.synthesize(
				request.text(),
				request.language(),
				request.voice(),
				request.style(),
				request.rate(),
				request.pitch(),
				request.volume());

		String audioPath = storageService.upload(
				audio,
				"speech.mp3");

		historyService.createRecord(SpeechHistory.builder()
				.text(request.text())
				.ownerId(currentUser.userId())
				.language(request.language())
				.voice(request.voice())
				.audioPath(audioPath)
				.build());

		utilization.setUtilized(Long.valueOf(utilization.getUtilized().intValue() + request.text().length()));
		metricsService.updateUsage(utilization);

		return audio;
	}

	@Override
	public byte[] download(UUID speechId) {
		SpeechHistory recording = historyService.getHistoryById(speechId);
		return storageService.download(recording.getAudioPath());
	}

}
