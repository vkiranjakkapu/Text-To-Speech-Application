package com.tts.reports.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record UserReportsDto(Long totalUsers, Double cumMonthlyAvg, List<DataRecord> monthlyReports) {

}
