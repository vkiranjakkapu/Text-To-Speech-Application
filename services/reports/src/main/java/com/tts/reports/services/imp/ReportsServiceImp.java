package com.tts.reports.services.imp;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.tts.reports.dto.TtsReportsDto;
import com.tts.reports.dto.UsageReportsDto;
import com.tts.reports.dto.UserReportsDto;
import com.tts.reports.dto.UserResponseDto;
import com.tts.reports.models.SpeechHistory;
import com.tts.reports.models.UsageMetrics;
import com.tts.reports.services.IdentityService;
import com.tts.reports.services.ReportsService;
import com.tts.reports.services.SpeechService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportsServiceImp implements ReportsService {

	private final SpeechService speechService;
	private final IdentityService identityService;

	@Override
	public UserReportsDto getUserReports() {
		List<UserResponseDto> allUsers = identityService.getAllUsers();

		LinkedHashMap<YearMonth, Long> monthlyReports = allUsers.stream()
				.sorted(Comparator.comparing(UserResponseDto::createdAt))
				.collect(Collectors.groupingBy(user -> YearMonth.from(user.createdAt()), LinkedHashMap::new,
						Collectors.counting()));

		double cumMonthlyAvg = monthlyReports.entrySet().stream().mapToLong(entry -> entry.getValue()).average()
				.getAsDouble();

		return UserReportsDto.builder()
				.totalUsers(Long.valueOf(allUsers.size()))
				.monthlyReports(monthlyReports)
				.cumMonthlyAvg(cumMonthlyAvg)
				.build();
	}

	@Override
	public UsageReportsDto getSynthesisReports() {
		List<UsageMetrics> allMetrics = speechService.getAllUsageMetrics();

		Map<YearMonth, List<UsageMetrics>> monthlyUsageRecords = allMetrics.stream()
				.sorted(Comparator.comparing(UsageMetrics::getMonth))
				.collect(Collectors.groupingBy(UsageMetrics::getMonth));

		List<YearMonth> months = IntStream.rangeClosed(0, 11)
				.mapToObj(i -> YearMonth.now().minusMonths(i))
				.sorted()
				.toList();

		Map<YearMonth, Long> monthlySynthesis = new HashMap<>();
		months.forEach(m -> {
			monthlySynthesis.put(m,
					Optional.ofNullable(monthlyUsageRecords.get(m))
							.map(monthlyRecords -> monthlyRecords.stream()
									.mapToLong(UsageMetrics::getUtilized).sum())
							.orElse(0l));
		});
		return UsageReportsDto.builder()
				.currentMonthUtilization(monthlySynthesis.get(YearMonth.now()))
				.monthlyUtilization(monthlySynthesis).build();
	}

	@Override
	public TtsReportsDto getTtsRequestsReports() {
		List<SpeechHistory> allHistoryRecords = speechService.getAllHistoryRecords();
		Map<YearMonth, Long> monthlyHistoryReports = allHistoryRecords.stream()
				.sorted(Comparator.comparing(SpeechHistory::getCreatedAt))
				.collect(Collectors.groupingBy(hist -> YearMonth.from(hist.getCreatedAt()),
						LinkedHashMap::new,
						Collectors.counting()));

		return TtsReportsDto.builder().currentRequests(monthlyHistoryReports.get(YearMonth.now()))
				.monthlyRequests(monthlyHistoryReports).build();

	}
}