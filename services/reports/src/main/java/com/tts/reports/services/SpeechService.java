package com.tts.reports.services;

import java.util.List;

import com.tts.reports.models.SpeechHistory;
import com.tts.reports.models.UsageMetrics;

public interface SpeechService {

    List<UsageMetrics> getAllUsageMetrics();

    List<SpeechHistory> getAllHistoryRecords();

}