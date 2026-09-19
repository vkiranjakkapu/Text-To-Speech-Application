package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tts.transform.models.UsageMetrics;
import com.tts.transform.properties.DefaultProperties;
import com.tts.transform.repositories.UsageMetricsRepository;
import com.tts.transform.services.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class UsageMetricsServiceImpTest {

    @Mock
    private UsageMetricsRepository usageRepository;

    @Mock
    private CurrentUserService currentUser;

    @Mock
    private DefaultProperties properties;

    @Mock
    private DefaultProperties.LimitEnforcements limits;

    @InjectMocks
    private UsageMetricsServiceImp usageMetricsService;

    private UUID userId;
    private YearMonth month;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        month = YearMonth.of(2026, 9);
    }

    @Test
    void getAllMetrics_shouldReturnAllMetrics() {
        List<UsageMetrics> metrics = List.of(
                UsageMetrics.builder()
                        .ownerId(UUID.randomUUID())
                        .month(month)
                        .utilized(100L)
                        .maxLimit(10000L)
                        .build(),
                UsageMetrics.builder()
                        .ownerId(UUID.randomUUID())
                        .month(month)
                        .utilized(250L)
                        .maxLimit(10000L)
                        .build());

        when(usageRepository.findAll()).thenReturn(metrics);

        List<UsageMetrics> result = usageMetricsService.getAllMetrics();

        assertSame(metrics, result);

        verify(usageRepository).findAll();
        verifyNoMoreInteractions(usageRepository);
    }

    @Test
    void getUtilizationByMonth_shouldReturnExistingMetrics() {
        UsageMetrics metrics = UsageMetrics.builder()
                .ownerId(userId)
                .month(month)
                .utilized(250L)
                .maxLimit(10000L)
                .build();

        when(currentUser.userId()).thenReturn(userId);
        when(usageRepository.findByOwnerIdAndMonth(userId, month))
                .thenReturn(Optional.of(metrics));

        UsageMetrics result = usageMetricsService.getUtilizationByMonth(month);

        assertSame(metrics, result);

        verify(currentUser).userId();
        verify(usageRepository).findByOwnerIdAndMonth(userId, month);
        verifyNoMoreInteractions(usageRepository);
    }

    @Test
    void getUtilizationByMonth_shouldCreateMetricsWhenNoRecordExists() {
        long maxMonthlyLimit = 10000L;

        when(currentUser.userId()).thenReturn(userId);
        when(usageRepository.findByOwnerIdAndMonth(userId, month))
                .thenReturn(Optional.empty());
        when(properties.getLimits()).thenReturn(limits);
        when(limits.getMaxMonthlyLimit()).thenReturn(maxMonthlyLimit);

        UsageMetrics savedMetrics = UsageMetrics.builder()
                .ownerId(userId)
                .month(month)
                .utilized(0L)
                .maxLimit(maxMonthlyLimit)
                .build();

        when(usageRepository.save(org.mockito.ArgumentMatchers.any(UsageMetrics.class)))
                .thenReturn(savedMetrics);

        UsageMetrics result = usageMetricsService.getUtilizationByMonth(month);

        assertSame(savedMetrics, result);

        verify(currentUser, times(2)).userId();
        verify(usageRepository).findByOwnerIdAndMonth(userId, month);
        verify(properties).getLimits();
        verify(limits).getMaxMonthlyLimit();

        verify(usageRepository).save(org.mockito.ArgumentMatchers.argThat(metrics ->
                metrics.getOwnerId().equals(userId)
                        && metrics.getMaxLimit().equals(maxMonthlyLimit)
                        && metrics.getUtilized().equals(0L)));

        verifyNoMoreInteractions(usageRepository);
    }

    @Test
    void updateUsage_shouldSaveAndReturnMetrics() {
        UsageMetrics metrics = UsageMetrics.builder()
                .ownerId(userId)
                .month(month)
                .utilized(500L)
                .maxLimit(10000L)
                .build();

        when(usageRepository.save(metrics)).thenReturn(metrics);

        UsageMetrics result = usageMetricsService.updateUsage(metrics);

        assertSame(metrics, result);

        verify(usageRepository).save(metrics);
        verifyNoMoreInteractions(usageRepository);
    }
}