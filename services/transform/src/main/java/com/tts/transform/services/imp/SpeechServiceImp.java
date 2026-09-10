package com.tts.transform.services.imp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tts.transform.dto.SynthesizeRequest;
import com.tts.transform.models.SpeechHistory;
import com.tts.transform.repositories.SpeechHistoryRepository;
import com.tts.transform.services.CurrentUserService;
import com.tts.transform.services.TtsProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpeechServiceImp {

    private final TtsProvider ttsProvider;
    private final SpeechHistoryRepository historyRepository;
    private final AzureBlobStorageService storageService;
    private final CurrentUserService currentUser;

    public byte[] synthesize(SynthesizeRequest request) {

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

        return audio;
    }

    public List<SpeechHistory> getMyHistory() {
        return historyRepository.findAllByOwnerId(currentUser.userId());
    }
}
