package com.tts.reports.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record TtsReportsDto(Long currentRequests, List<DataRecord> monthlyRequests) {

}
