package com.tts.transform.models;

import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tts.transform.models.config.YearMonthConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usage_metrics")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsageMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID ownerId;

    @Convert(converter = YearMonthConverter.class)
    @Builder.Default
    private YearMonth month = YearMonth.now();

    @Builder.Default
    private Long utilized = 0l;

    @Builder.Default
    private Long maxLimit = 10000l;

    @UpdateTimestamp
    private Instant updatedAt;

    @CreationTimestamp
    private Instant createdAt;

}
