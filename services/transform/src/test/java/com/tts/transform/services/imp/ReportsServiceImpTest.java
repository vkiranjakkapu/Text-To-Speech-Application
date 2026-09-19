package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tts.transform.models.SpeechHistory;
import com.tts.transform.models.UsageMetrics;
import com.tts.transform.services.SpeechHistoryService;
import com.tts.transform.services.UsageMetricsService;

class ReportsServiceImpTest {

	private UsageMetricsService metricsService;

	private SpeechHistoryService historyService;

	private ReportsServiceImp service;

	@BeforeEach
	void setUp() {

		metricsService = mock(UsageMetricsService.class);
		historyService = mock(SpeechHistoryService.class);

		service = new ReportsServiceImp(
				metricsService,
				historyService);
	}

	@Test
	void getAllUsageMetrics_shouldReturnAllMetrics() {

		UsageMetrics metric1 = mock(UsageMetrics.class);
		UsageMetrics metric2 = mock(UsageMetrics.class);

		List<UsageMetrics> metrics = List.of(metric1, metric2);

		when(metricsService.getAllMetrics())
				.thenReturn(metrics);

		List<UsageMetrics> result =
				service.getAllUsageMetrics();

		assertEquals(metrics, result);

		verify(metricsService).getAllMetrics();
	}

	@Test
	void getAllHistoryRecords_shouldReturnAllHistoryRecords() {

		SpeechHistory history1 = mock(SpeechHistory.class);
		SpeechHistory history2 = mock(SpeechHistory.class);

		List<SpeechHistory> history =
				List.of(history1, history2);

		when(historyService.getAllHistoryRecords())
				.thenReturn(history);

		List<SpeechHistory> result =
				service.getAllHistoryRecords();

		assertEquals(history, result);

		verify(historyService).getAllHistoryRecords();
	}

	@Test
	void getAllUsageMetrics_shouldReturnEmptyListWhenNoMetricsExist() {

		when(metricsService.getAllMetrics())
				.thenReturn(List.of());

		List<UsageMetrics> result =
				service.getAllUsageMetrics();

		assertEquals(List.of(), result);

		verify(metricsService).getAllMetrics();
	}

	@Test
	void getAllHistoryRecords_shouldReturnEmptyListWhenNoHistoryExists() {

		when(historyService.getAllHistoryRecords())
				.thenReturn(List.of());

		List<SpeechHistory> result =
				service.getAllHistoryRecords();

		assertEquals(List.of(), result);

		verify(historyService).getAllHistoryRecords();
	}
}