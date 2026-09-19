package com.tts.transform.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.models.SpeechHistory;
import com.tts.transform.models.UsageMetrics;
import com.tts.transform.services.ReportsService;

class ReportsControllerTest {

	private ReportsService reportsService;

	private ReportsController controller;

	@BeforeEach
	void setUp() {

		reportsService = mock(ReportsService.class);

		controller = new ReportsController(reportsService);
	}

	@Test
	void synthesis_shouldReturnAllUsageMetrics() {

		UsageMetrics metric1 = mock(UsageMetrics.class);
		UsageMetrics metric2 = mock(UsageMetrics.class);

		List<UsageMetrics> metrics =
				List.of(metric1, metric2);

		when(reportsService.getAllUsageMetrics())
				.thenReturn(metrics);

		ResponseEntity<ApiResponseDto> response =
				controller.synthesis();

		assertEquals(
				metrics,
				response.getBody().data());

		verify(reportsService)
				.getAllUsageMetrics();
	}

	@Test
	void synthesis_shouldReturnEmptyListWhenNoMetricsExist() {

		when(reportsService.getAllUsageMetrics())
				.thenReturn(List.of());

		ResponseEntity<ApiResponseDto> response =
				controller.synthesis();

		assertEquals(
				List.of(),
				response.getBody().data());

		verify(reportsService)
				.getAllUsageMetrics();
	}

	@Test
	void requests_shouldReturnAllHistoryRecords() {

		SpeechHistory history1 = mock(SpeechHistory.class);
		SpeechHistory history2 = mock(SpeechHistory.class);

		List<SpeechHistory> history =
				List.of(history1, history2);

		when(reportsService.getAllHistoryRecords())
				.thenReturn(history);

		ResponseEntity<ApiResponseDto> response =
				controller.requests();

		assertEquals(
				history,
				response.getBody().data());

		verify(reportsService)
				.getAllHistoryRecords();
	}

	@Test
	void requests_shouldReturnEmptyListWhenNoHistoryExists() {

		when(reportsService.getAllHistoryRecords())
				.thenReturn(List.of());

		ResponseEntity<ApiResponseDto> response =
				controller.requests();

		assertEquals(
				List.of(),
				response.getBody().data());

		verify(reportsService)
				.getAllHistoryRecords();
	}
}