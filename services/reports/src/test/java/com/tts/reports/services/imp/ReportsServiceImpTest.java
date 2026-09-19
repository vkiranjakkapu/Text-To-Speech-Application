package com.tts.reports.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tts.reports.dto.DataRecord;
import com.tts.reports.dto.TtsReportsDto;
import com.tts.reports.dto.UsageReportsDto;
import com.tts.reports.dto.UserReportsDto;
import com.tts.reports.dto.UserResponseDto;
import com.tts.reports.models.SpeechHistory;
import com.tts.reports.models.UsageMetrics;
import com.tts.reports.services.IdentityService;
import com.tts.reports.services.SpeechService;

@ExtendWith(MockitoExtension.class)
class ReportsServiceImpTest {

	@Mock
	private SpeechService speechService;

	@Mock
	private IdentityService identityService;

	@InjectMocks
	private ReportsServiceImp reportsService;

	private YearMonth currentMonth;

	@BeforeEach
	void setUp() {
		currentMonth = YearMonth.now();
	}

	@Test
	void getUserReports_shouldReturnMonthlyUserReports() {

		UserResponseDto user1 = UserResponseDto.builder()
				.id(UUID.randomUUID())
				.createdAt(currentMonth.atDay(5).atStartOfDay())
				.build();

		UserResponseDto user2 = UserResponseDto.builder()
				.id(UUID.randomUUID())
				.createdAt(currentMonth.atDay(10).atStartOfDay())
				.build();

		UserResponseDto user3 = UserResponseDto.builder()
				.id(UUID.randomUUID())
				.createdAt(currentMonth.minusMonths(1).atDay(15).atStartOfDay())
				.build();

		when(identityService.getAllUsers())
				.thenReturn(List.of(user1, user2, user3));

		UserReportsDto result = reportsService.getUserReports();

		assertNotNull(result);
		assertEquals(3L, result.totalUsers());
		assertEquals(1.5, result.cumMonthlyAvg());

		assertEquals(12, result.monthlyReports().size());

		DataRecord currentMonthRecord = result.monthlyReports().get(result.monthlyReports().size() - 1);

		assertEquals(2L, currentMonthRecord.value());

		verify(identityService).getAllUsers();
	}

	@Test
	void getUserReports_shouldReturnDailyReportsForMonth() {

		YearMonth month = YearMonth.of(2026, 9);

		UserResponseDto user1 = UserResponseDto.builder()
				.id(UUID.randomUUID())
				.createdAt(LocalDateTime.of(2026, 9, 5, 10, 0))
				.build();

		UserResponseDto user2 = UserResponseDto.builder()
				.id(UUID.randomUUID())
				.createdAt(LocalDateTime.of(2026, 9, 5, 15, 0))
				.build();

		UserResponseDto user3 = UserResponseDto.builder()
				.id(UUID.randomUUID())
				.createdAt(LocalDateTime.of(2026, 9, 10, 10, 0))
				.build();

		UserResponseDto user4 = UserResponseDto.builder()
				.id(UUID.randomUUID())
				.createdAt(LocalDateTime.of(2026, 8, 20, 10, 0))
				.build();

		when(identityService.getAllUsers())
				.thenReturn(List.of(user1, user2, user3, user4));

		List<DataRecord> result = reportsService.getUserReports(month);

		assertEquals(30, result.size());

		assertEquals(2L, result.get(4).value());
		assertEquals("5th, Sep", result.get(4).key());

		assertEquals(1L, result.get(9).value());
		assertEquals("10th, Sep", result.get(9).key());

		assertEquals(0L, result.get(0).value());

		verify(identityService).getAllUsers();
	}

	@Test
	void getSynthesisReports_shouldReturnMonthlyUtilization() {

		UsageMetrics current = UsageMetrics.builder()
				.month(currentMonth)
				.utilized(500L)
				.build();

		UsageMetrics previous = UsageMetrics.builder()
				.month(currentMonth.minusMonths(1))
				.utilized(300L)
				.build();

		UsageMetrics currentSecondRecord = UsageMetrics.builder()
				.month(currentMonth)
				.utilized(200L)
				.build();

		when(speechService.getAllUsageMetrics())
				.thenReturn(List.of(current, previous, currentSecondRecord));

		UsageReportsDto result = reportsService.getSynthesisReports();

		assertNotNull(result);
		assertEquals(700L, result.currentMonthUtilization());
		assertEquals(12, result.monthlyUtilization().size());

		DataRecord currentRecord = result.monthlyUtilization().get(result.monthlyUtilization().size() - 1);

		assertEquals(700L, currentRecord.value());

		verify(speechService).getAllUsageMetrics();
	}

	@Test
	void getSynthesisReports_shouldReturnDailyCharacterUtilization() {

		YearMonth month = YearMonth.of(2026, 9);

		SpeechHistory history1 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.text("Hello")
				.createdAt(LocalDateTime.of(2026, 9, 5, 10, 0))
				.build();

		SpeechHistory history2 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.text("Welcome")
				.createdAt(LocalDateTime.of(2026, 9, 5, 15, 0))
				.build();

		SpeechHistory history3 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.text("Transform")
				.createdAt(LocalDateTime.of(2026, 9, 10, 10, 0))
				.build();

		SpeechHistory history4 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.text("Ignored")
				.createdAt(LocalDateTime.of(2026, 8, 20, 10, 0))
				.build();

		when(speechService.getAllHistoryRecords())
				.thenReturn(List.of(history1, history2, history3, history4));

		List<DataRecord> result = reportsService.getSynthesisReports(month);

		assertEquals(30, result.size());

		assertEquals(12L, result.get(4).value());
		assertEquals("5th, Sep", result.get(4).key());

		assertEquals(9L, result.get(9).value());
		assertEquals("10th, Sep", result.get(9).key());

		assertEquals(0L, result.get(0).value());

		verify(speechService).getAllHistoryRecords();
	}

	@Test
	void getTtsRequestsReports_shouldReturnMonthlyRequestCounts() {

		SpeechHistory current1 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.createdAt(currentMonth.atDay(5).atStartOfDay())
				.build();

		SpeechHistory current2 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.createdAt(currentMonth.atDay(10).atStartOfDay())
				.build();

		SpeechHistory previous = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.createdAt(currentMonth.minusMonths(1).atDay(15).atStartOfDay())
				.build();

		when(speechService.getAllHistoryRecords())
				.thenReturn(List.of(current1, current2, previous));

		TtsReportsDto result = reportsService.getTtsRequestsReports();

		assertNotNull(result);
		assertEquals(2L, result.currentRequests());
		assertEquals(12, result.monthlyRequests().size());

		DataRecord currentRecord = result.monthlyRequests().get(result.monthlyRequests().size() - 1);

		assertEquals(2L, currentRecord.value());

		verify(speechService).getAllHistoryRecords();
	}

	@Test
	void getTtsRequestsReports_shouldReturnDailyRequestCounts() {

		YearMonth month = YearMonth.of(2026, 9);

		SpeechHistory history1 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.createdAt(LocalDateTime.of(2026, 9, 5, 10, 0))
				.build();

		SpeechHistory history2 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.createdAt(LocalDateTime.of(2026, 9, 5, 15, 0))
				.build();

		SpeechHistory history3 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.createdAt(LocalDateTime.of(2026, 9, 10, 10, 0))
				.build();

		SpeechHistory history4 = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.createdAt(LocalDateTime.of(2026, 8, 20, 10, 0))
				.build();

		when(speechService.getAllHistoryRecords())
				.thenReturn(List.of(history1, history2, history3, history4));

		List<DataRecord> result = reportsService.getTtsRequestsReports(month);

		assertEquals(30, result.size());

		assertEquals(2L, result.get(4).value());
		assertEquals("5th, Sep", result.get(4).key());

		assertEquals(1L, result.get(9).value());
		assertEquals("10th, Sep", result.get(9).key());

		assertEquals(0L, result.get(0).value());

		verify(speechService).getAllHistoryRecords();
	}
}