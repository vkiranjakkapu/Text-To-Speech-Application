package com.tts.reports.controllers;

import java.time.YearMonth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tts.reports.dto.ApiResponseDto;
import com.tts.reports.services.ReportsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports/api/v1/speech")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService reportsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto> getUserStats() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getUserReports()).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{month}")
    public ResponseEntity<ApiResponseDto> getUserStatsByMonth(@PathVariable YearMonth month) {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getUserReports(month)).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/synthesis")
    public ResponseEntity<?> synthesis() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getSynthesisReports()).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/synthesis/{month}")
    public ResponseEntity<?> synthesisByMonth(@PathVariable YearMonth month) {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getSynthesisReports(month)).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requests")
    public ResponseEntity<?> requests() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getTtsRequestsReports()).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requests/{month}")
    public ResponseEntity<?> requestsByMonth(@PathVariable YearMonth month) {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getTtsRequestsReports(month)).build());
    }

}