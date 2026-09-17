package com.tts.reports.services.imp;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.tts.reports.dto.DataRecord;
import com.tts.reports.dto.TtsReportsDto;
import com.tts.reports.dto.UsageReportsDto;
import com.tts.reports.dto.UserReportsDto;
import com.tts.reports.dto.UserResponseDto;
import com.tts.reports.models.UsageMetrics;
import com.tts.reports.services.IdentityService;
import com.tts.reports.services.ReportsService;
import com.tts.reports.services.SpeechService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportsServiceImp implements ReportsService {

	private static final int REPORT_MONTHS = 12;

	private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM''yy", Locale.ENGLISH);

	private final SpeechService speechService;
	private final IdentityService identityService;

	@Override
	public UserReportsDto getUserReports() {

		List<UserResponseDto> allUsers = identityService.getAllUsers();

		Map<YearMonth, Long> monthlyReports = allUsers.stream()
				.collect(Collectors.groupingBy(
						user -> YearMonth.from(user.createdAt()),
						TreeMap::new,
						Collectors.counting()));

		double cumMonthlyAvg = monthlyReports.values()
				.stream()
				.mapToLong(Long::longValue)
				.average()
				.orElse(0);

		Map<YearMonth, Long> reports = getMonths()
				.stream()
				.collect(Collectors.toMap(
						month -> month,
						month -> monthlyReports.getOrDefault(month, 0L),
						(a, b) -> a,
						TreeMap::new));

		return UserReportsDto.builder()
				.totalUsers((long) allUsers.size())
				.monthlyReports(
						reports.entrySet()
								.stream()
								.map(this::mapToMonthlyChartData)
								.toList())
				.cumMonthlyAvg(cumMonthlyAvg)
				.build();
	}

	@Override
	public List<DataRecord> getUserReports(YearMonth month) {

		Map<LocalDate, Long> dailyReports = identityService.getAllUsers()
				.stream()
				.filter(user -> YearMonth.from(user.createdAt()).equals(month))
				.collect(Collectors.groupingBy(
						user -> LocalDate.from(user.createdAt()),
						Collectors.counting()));

		return getDays(month)
				.stream()
				.map(day -> DataRecord.builder()
						.key(formatDate(day))
						.value(dailyReports.getOrDefault(day, 0L))
						.build())
				.toList();
	}

	@Override
	public UsageReportsDto getSynthesisReports() {

		Map<YearMonth, Long> monthlySynthesis = speechService.getAllUsageMetrics()
				.stream()
				.collect(Collectors.groupingBy(
						UsageMetrics::getMonth,
						TreeMap::new,
						Collectors.summingLong(UsageMetrics::getUtilized)));

		Map<YearMonth, Long> reports = getMonths()
				.stream()
				.collect(Collectors.toMap(
						month -> month,
						month -> monthlySynthesis.getOrDefault(month, 0L),
						(a, b) -> a,
						TreeMap::new));

		return UsageReportsDto.builder()
				.currentMonthUtilization(
						reports.getOrDefault(YearMonth.now(), 0L))
				.monthlyUtilization(
						reports.entrySet()
								.stream()
								.map(this::mapToMonthlyChartData)
								.toList())
				.build();
	}

	@Override
	public List<DataRecord> getSynthesisReports(YearMonth month) {

		Map<LocalDate, Long> dailyRecords = speechService.getAllHistoryRecords()
				.stream()
				.filter(history -> YearMonth.from(history.getCreatedAt()).equals(month))
				.collect(Collectors.groupingBy(
						history -> LocalDate.from(history.getCreatedAt()),
						Collectors.summingLong(
								history -> history.getText().length())));

		return getDays(month)
				.stream()
				.map(day -> DataRecord.builder()
						.key(formatDate(day))
						.value(dailyRecords.getOrDefault(day, 0L))
						.build())
				.toList();
	}

	@Override
	public TtsReportsDto getTtsRequestsReports() {

		Map<YearMonth, Long> monthlyRequests = speechService.getAllHistoryRecords()
				.stream()
				.collect(Collectors.groupingBy(
						history -> YearMonth.from(history.getCreatedAt()),
						TreeMap::new,
						Collectors.counting()));

		Map<YearMonth, Long> reports = getMonths()
				.stream()
				.collect(Collectors.toMap(
						month -> month,
						month -> monthlyRequests.getOrDefault(month, 0L),
						(a, b) -> a,
						TreeMap::new));

		return TtsReportsDto.builder()
				.currentRequests(
						reports.getOrDefault(YearMonth.now(), 0L))
				.monthlyRequests(
						reports.entrySet()
								.stream()
								.map(this::mapToMonthlyChartData)
								.toList())
				.build();
	}

	@Override
	public List<DataRecord> getTtsRequestsReports(YearMonth month) {

		Map<LocalDate, Long> dailyRequests = speechService.getAllHistoryRecords()
				.stream()
				.filter(history -> YearMonth.from(history.getCreatedAt()).equals(month))
				.collect(Collectors.groupingBy(
						history -> LocalDate.from(history.getCreatedAt()),
						Collectors.counting()));

		return getDays(month)
				.stream()
				.map(day -> DataRecord.builder()
						.key(formatDate(day))
						.value(dailyRequests.getOrDefault(day, 0L))
						.build())
				.toList();
	}

	private List<YearMonth> getMonths() {
		return IntStream.rangeClosed(0, REPORT_MONTHS - 1)
				.mapToObj(i -> YearMonth.now().minusMonths(i))
				.sorted()
				.toList();
	}

	private List<LocalDate> getDays(YearMonth month) {
		return IntStream.rangeClosed(1, month.lengthOfMonth())
				.mapToObj(month::atDay)
				.toList();
	}

	private DataRecord mapToMonthlyChartData(
			Map.Entry<YearMonth, Long> entry) {

		return DataRecord.builder()
				.key(entry.getKey().format(MONTH_FORMATTER))
				.value(entry.getValue())
				.build();
	}

	private static String formatDate(LocalDate date) {
		int day = date.getDayOfMonth();

		String suffix = switch (day % 100) {
			case 11, 12, 13 -> "th";
			default -> switch (day % 10) {
				case 1 -> "st";
				case 2 -> "nd";
				case 3 -> "rd";
				default -> "th";
			};
		};

		return day + suffix + ", " +
				date.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH));
	}
}