package com.tts.reports.services;

import java.time.YearMonth;
import java.util.List;

import com.tts.reports.dto.DataRecord;
import com.tts.reports.dto.TtsReportsDto;
import com.tts.reports.dto.UsageReportsDto;
import com.tts.reports.dto.UserReportsDto;

public interface ReportsService {

    UserReportsDto getUserReports();

    List<DataRecord> getUserReports(YearMonth month);

    UsageReportsDto getSynthesisReports();

    List<DataRecord> getSynthesisReports(YearMonth month);

    TtsReportsDto getTtsRequestsReports();

    List<DataRecord> getTtsRequestsReports(YearMonth month);
}