package com.tts.reports.services.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import com.tts.reports.dto.RestResponseDto;
import com.tts.reports.exceptions.BusinessException;
import com.tts.reports.exceptions.InternalCommunicationException;
import com.tts.reports.models.SpeechHistory;
import com.tts.reports.models.UsageMetrics;
import com.tts.reports.services.SpeechService;

@Service
public class SpeechServiceImp implements SpeechService {

    private final RestClient restClient;

    @Value("${services.uri.transform}")
    private String SPEECH_SERVICE_URL;

    public SpeechServiceImp(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public List<UsageMetrics> getAllUsageMetrics() {
        try {
            return restClient.get().uri(SPEECH_SERVICE_URL + "/reports/metrics").retrieve()
                    .body(new ParameterizedTypeReference<RestResponseDto<List<UsageMetrics>>>() {
                    }).getData();
        } catch (HttpStatusCodeException e) {
            String rawJsonResponseBody = e.getResponseBodyAsString();
            throw new InternalCommunicationException(rawJsonResponseBody, e.getStatusCode());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }

    }

    @Override
    public List<SpeechHistory> getAllHistoryRecords() {
        try {
            return restClient.get().uri(SPEECH_SERVICE_URL + "/reports/history").retrieve()
                    .body(new ParameterizedTypeReference<RestResponseDto<List<SpeechHistory>>>() {
                    }).getData();
        } catch (HttpStatusCodeException e) {
            String rawJsonResponseBody = e.getResponseBodyAsString();
            throw new InternalCommunicationException(rawJsonResponseBody, e.getStatusCode());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }

    }
}
