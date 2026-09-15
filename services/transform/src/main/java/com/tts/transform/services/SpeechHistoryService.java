package com.tts.transform.services;

import java.util.List;

import com.tts.transform.models.SpeechHistory;

public interface SpeechHistoryService {

    List<SpeechHistory> getAllHistoryRecords();

}