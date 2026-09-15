package com.tts.reports.dto;

import java.time.YearMonth;
import java.util.Map;

import lombok.Builder;

@Builder
public record UsageReportsDto(Long currentMonthUtilization, Map<YearMonth, Long> monthlyUtilization) {

}
