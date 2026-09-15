package com.tts.transform.services.imp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tts.transform.models.SpeechHistory;
import com.tts.transform.models.UsageMetrics;
import com.tts.transform.services.ReportsService;
import com.tts.transform.services.SpeechHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportsServiceImp implements ReportsService {

	private final UsageMetricsService metricsService;
	private final SpeechHistoryService historyService;

	@Override
	public List<UsageMetrics> getAllUsageMetrics() {
		return metricsService.getAllMetrics();
	}

	@Override
	public List<SpeechHistory> getAllHistoryRecords() {
		return historyService.getAllHistoryRecords();
	}
}