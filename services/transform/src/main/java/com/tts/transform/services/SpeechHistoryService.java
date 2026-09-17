package com.tts.transform.services;

import java.util.List;
import java.util.UUID;

import com.tts.transform.dto.SpeechHistoryDto;
import com.tts.transform.models.SpeechHistory;

public interface SpeechHistoryService {

    SpeechHistory createRecord(SpeechHistory speechHistory);

    SpeechHistory getHistoryById(UUID id);

    List<SpeechHistory> getMyHistory();

    List<SpeechHistory> getAllHistoryRecords();

    List<SpeechHistoryDto> getAllHistoryRecordsMap();

    boolean deleteHistoryRecord(UUID histId);

}