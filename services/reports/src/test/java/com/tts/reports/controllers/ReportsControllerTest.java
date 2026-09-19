package com.tts.reports.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.tts.reports.dto.ApiResponseDto;
import com.tts.reports.dto.DataRecord;
import com.tts.reports.dto.TtsReportsDto;
import com.tts.reports.dto.UsageReportsDto;
import com.tts.reports.dto.UserReportsDto;
import com.tts.reports.services.ReportsService;

@ExtendWith(MockitoExtension.class)
class ReportsControllerTest {

	@Mock
	private ReportsService reportsService;

	@InjectMocks
	private ReportsController reportsController;

	private UserReportsDto userReports;
	private UsageReportsDto usageReports;
	private TtsReportsDto ttsReports;

	@BeforeEach
	void setUp() {

		userReports = UserReportsDto.builder()
				.totalUsers(10L)
				.cumMonthlyAvg(2.5)
				.monthlyReports(List.of(
						new DataRecord("Sep'26", 3L)))
				.build();

		usageReports = UsageReportsDto.builder()
				.currentMonthUtilization(500L)
				.monthlyUtilization(List.of(
						new DataRecord("Sep'26", 500L)))
				.build();

		ttsReports = TtsReportsDto.builder()
				.currentRequests(8L)
				.monthlyRequests(List.of(
						new DataRecord("Sep'26", 8L)))
				.build();
	}

	@Test
	void getUserStats_shouldReturnUserReports() {

		when(reportsService.getUserReports())
				.thenReturn(userReports);

		ResponseEntity<ApiResponseDto> response = reportsController.getUserStats();

		assertNotNull(response);
		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());

		assertEquals(userReports, response.getBody().data());

		verify(reportsService).getUserReports();
	}

	@Test
	void getUserStatsByMonth_shouldReturnUserReportsForMonth() {

		YearMonth month = YearMonth.of(2026, 9);

		List<DataRecord> records = List.of(
				new DataRecord("5th, Sep", 2L));

		when(reportsService.getUserReports(month))
				.thenReturn(records);

		ResponseEntity<ApiResponseDto> response = reportsController.getUserStatsByMonth(month);

		assertNotNull(response);
		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());

		assertEquals(records, response.getBody().data());

		verify(reportsService).getUserReports(month);
	}

	@Test
	void synthesis_shouldReturnUsageReports() {

		when(reportsService.getSynthesisReports())
				.thenReturn(usageReports);

		ResponseEntity<?> response = reportsController.synthesis();

		assertNotNull(response);
		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());

		ApiResponseDto body = (ApiResponseDto) response.getBody();

		assertEquals(usageReports, body.data());

		verify(reportsService).getSynthesisReports();
	}

	@Test
	void synthesisByMonth_shouldReturnUsageReportsForMonth() {

		YearMonth month = YearMonth.of(2026, 9);

		List<DataRecord> records = List.of(
				new DataRecord("5th, Sep", 120L));

		when(reportsService.getSynthesisReports(month))
				.thenReturn(records);

		ResponseEntity<?> response = reportsController.synthesisByMonth(month);

		assertNotNull(response);
		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());

		ApiResponseDto body = (ApiResponseDto) response.getBody();

		assertEquals(records, body.data());

		verify(reportsService).getSynthesisReports(month);
	}

	@Test
	void requests_shouldReturnTtsReports() {

		when(reportsService.getTtsRequestsReports())
				.thenReturn(ttsReports);

		ResponseEntity<?> response = reportsController.requests();

		assertNotNull(response);
		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());

		ApiResponseDto body = (ApiResponseDto) response.getBody();

		assertEquals(ttsReports, body.data());

		verify(reportsService).getTtsRequestsReports();
	}

	@Test
	void requestsByMonth_shouldReturnTtsReportsForMonth() {

		YearMonth month = YearMonth.of(2026, 9);

		List<DataRecord> records = List.of(
				new DataRecord("5th, Sep", 2L));

		when(reportsService.getTtsRequestsReports(month))
				.thenReturn(records);

		ResponseEntity<?> response = reportsController.requestsByMonth(month);

		assertNotNull(response);
		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());

		ApiResponseDto body = (ApiResponseDto) response.getBody();

		assertEquals(records, body.data());

		verify(reportsService).getTtsRequestsReports(month);
	}
}