package com.tts.transform.services.imp;

import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tts.transform.models.UsageMetrics;
import com.tts.transform.properties.DefaultProperties;
import com.tts.transform.repositories.UsageMetricsRepository;
import com.tts.transform.services.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsageMetricsService {

    private final UsageMetricsRepository usageRepository;
    private final CurrentUserService currentUser;

    private final DefaultProperties properties;

    public List<UsageMetrics> getAllMetrics() {
        return usageRepository.findAll();
    }

    public UsageMetrics getUtilizationByMonth(YearMonth month) {
        return usageRepository.findByOwnerIdAndMonth(currentUser.userId(), month).orElseGet(() -> {
            return usageRepository.save(UsageMetrics.builder()
                    .ownerId(currentUser.userId())
                    .maxLimit(properties.getLimits().getMaxMonthlyLimit())
                    .month(YearMonth.now())
                    .build());
        });
    }

    public UsageMetrics updateUsage(UsageMetrics metrics) {
        return usageRepository.save(metrics);
    }

}
