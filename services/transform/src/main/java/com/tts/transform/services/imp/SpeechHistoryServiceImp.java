package com.tts.transform.services.imp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tts.transform.models.SpeechHistory;
import com.tts.transform.repositories.SpeechHistoryRepository;
import com.tts.transform.services.SpeechHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class SpeechHistoryServiceImp implements SpeechHistoryService {

    private final SpeechHistoryRepository historyRepository;

    @Override
    public List<SpeechHistory> getAllHistoryRecords() {
        return historyRepository.findAll();
    }
    
}
