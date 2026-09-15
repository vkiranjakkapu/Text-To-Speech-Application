package com.tts.transform.services;

import java.time.YearMonth;
import java.util.List;

import com.tts.transform.models.UsageMetrics;

public interface UsageMetricsService {

    List<UsageMetrics> getAllMetrics();

    UsageMetrics getUtilizationByMonth(YearMonth month);

    UsageMetrics updateUsage(UsageMetrics metrics);

}