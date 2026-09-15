package com.tts.reports.models;

import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsageMetrics {

    private UUID id;

    private UUID ownerId;

    @Builder.Default
    private YearMonth month = YearMonth.now();

    @Builder.Default
    private Long utilized = 0l;

    @Builder.Default
    private Long maxLimit = 10000l;

    private Instant updatedAt;

    private Instant createdAt;

}
