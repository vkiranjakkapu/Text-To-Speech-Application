package com.tts.transform.repositories;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tts.transform.models.UsageMetrics;

public interface UsageMetricsRepository extends JpaRepository<UsageMetrics, UUID> {

    Optional<UsageMetrics> findByOwnerIdAndMonth(UUID ownerId, YearMonth month);

}
