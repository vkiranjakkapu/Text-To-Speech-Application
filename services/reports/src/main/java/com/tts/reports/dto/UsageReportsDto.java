package com.tts.reports.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record UsageReportsDto(Long currentMonthUtilization, List<DataRecord> monthlyUtilization) {

}
