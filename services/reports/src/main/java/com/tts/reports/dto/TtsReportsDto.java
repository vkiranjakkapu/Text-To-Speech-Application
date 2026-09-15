package com.tts.reports.dto;

import java.time.YearMonth;
import java.util.Map;

import lombok.Builder;

@Builder
public record TtsReportsDto(Long currentRequests, Map<YearMonth, Long> monthlyRequests) {

}
