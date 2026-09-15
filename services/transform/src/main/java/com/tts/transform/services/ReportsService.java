package com.tts.transform.services;

import java.util.List;

import com.tts.transform.models.SpeechHistory;
import com.tts.transform.models.UsageMetrics;

public interface ReportsService {

    List<UsageMetrics> getAllUsageMetrics();

    List<SpeechHistory> getAllHistoryRecords();
}