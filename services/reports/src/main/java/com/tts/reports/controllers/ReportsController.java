package com.tts.reports.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tts.reports.dto.ApiResponseDto;
import com.tts.reports.services.ReportsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService reportsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto> getUserStats() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getUserReports()).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/synthesis")
    public ResponseEntity<?> synthesis() {
        return ResponseEntity.ok(reportsService.getSynthesisReports());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requests")
    public ResponseEntity<?> requests() {
        return ResponseEntity.ok(reportsService.getTtsRequestsReports());
    }

}