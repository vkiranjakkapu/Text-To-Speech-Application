package com.tts.transform.services.imp;

import java.time.YearMonth;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tts.transform.dto.SynthesizeRequest;
import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.models.SpeechHistory;
import com.tts.transform.models.UsageMetrics;
import com.tts.transform.repositories.SpeechHistoryRepository;
import com.tts.transform.services.CurrentUserService;
import com.tts.transform.services.TtsProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpeechServiceImp {

    private final TtsProvider ttsProvider;
    private final SpeechHistoryRepository historyRepository;
    private final UsageMetricsService metricsService;
    private final AzureBlobStorageService storageService;
    private final CurrentUserService currentUser;

    public byte[] synthesize(SynthesizeRequest request) {

        UsageMetrics utilization = metricsService.getUtilizationByMonth(YearMonth.now());

        long remaining = utilization.getMaxLimit() - utilization.getUtilized();

        if (remaining <= 0) {
            String message = "Your Monthly Limit Exhausted.";

            if (Math.abs(remaining) > 25) {
                message += " You Have Already Exceeded Your Monthly Limit By "
                        + Math.abs(remaining) + " Characters";
            }
            throw new BusinessException(BusinessExceptions.USAGE_LIMIT_EXHAUSTED, message.toString(),
                    HttpStatus.TOO_MANY_REQUESTS);
        }

        byte[] audio = ttsProvider.synthesize(
                request.text(),
                request.language(),
                request.voice());

        String audioPath = storageService.upload(
                audio,
                "speech.mp3");

        historyRepository.save(
                SpeechHistory.builder()
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

    public List<SpeechHistory> getMyHistory() {
        return historyRepository.findAllByOwnerId(currentUser.userId());
    }
}
