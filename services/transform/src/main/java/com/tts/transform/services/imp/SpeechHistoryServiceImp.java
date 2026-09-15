package com.tts.transform.services.imp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tts.transform.models.SpeechHistory;
import com.tts.transform.repositories.SpeechHistoryRepository;
import com.tts.transform.services.CurrentUserService;
import com.tts.transform.services.SpeechHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpeechHistoryServiceImp implements SpeechHistoryService {

    private final SpeechHistoryRepository historyRepository;
    private final CurrentUserService currentUser;

    @Override
    public List<SpeechHistory> getMyHistory() {
        return historyRepository.findAllByOwnerId(currentUser.userId());
    }

    @Override
    public List<SpeechHistory> getAllHistoryRecords() {
        return historyRepository.findAll();
    }

}
