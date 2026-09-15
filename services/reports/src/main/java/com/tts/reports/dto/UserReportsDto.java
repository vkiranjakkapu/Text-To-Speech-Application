package com.tts.reports.dto;

import java.time.YearMonth;
import java.util.Map;

import lombok.Builder;

@Builder
public record UserReportsDto(Long totalUsers, Double cumMonthlyAvg, Map<YearMonth, Long> monthlyReports) {

}
