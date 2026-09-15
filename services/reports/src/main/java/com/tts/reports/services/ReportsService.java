package com.tts.reports.services;

import com.tts.reports.dto.TtsReportsDto;
import com.tts.reports.dto.UsageReportsDto;
import com.tts.reports.dto.UserReportsDto;

public interface ReportsService {

    UserReportsDto getUserReports();
    
    UsageReportsDto getSynthesisReports();

    TtsReportsDto getTtsRequestsReports();
}