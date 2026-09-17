package com.tts.transform.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.services.ReportsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService reportsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/metrics")
    public ResponseEntity<ApiResponseDto> synthesis() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getAllUsageMetrics()).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/history")
    public ResponseEntity<ApiResponseDto> requests() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getAllHistoryRecords()).build());
    }

}