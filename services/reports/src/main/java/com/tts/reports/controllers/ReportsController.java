package com.tts.reports.controllers;

import java.time.YearMonth;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.web.model.ErrorResponse;
import com.tts.reports.dto.ApiResponseDto;
import com.tts.reports.services.ReportsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports/api/v1/speech")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService reportsService;

    @Operation(summary = "Get user statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto> getUserStats() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getUserReports()).build());
    }

    @Operation(summary = "Get user statistics by month")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User statistics for the specified month retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid month format", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{month}")
    public ResponseEntity<ApiResponseDto> getUserStatsByMonth(@PathVariable YearMonth month) {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getUserReports(month)).build());
    }

    @Operation(summary = "Get speech synthesis usage statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Speech synthesis usage statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/synthesis")
    public ResponseEntity<?> synthesis() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getSynthesisReports()).build());
    }

    @Operation(summary = "Get speech synthesis usage statistics by month")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Speech synthesis usage statistics for the specified month retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid month format", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/synthesis/{month}")
    public ResponseEntity<?> synthesisByMonth(@PathVariable YearMonth month) {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getSynthesisReports(month)).build());
    }

    @Operation(summary = "Get text-to-speech request statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Text-to-speech request statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requests")
    public ResponseEntity<?> requests() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getTtsRequestsReports()).build());
    }

    @Operation(summary = "Get text-to-speech request statistics by month")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Text-to-speech request statistics for the specified month retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid month format", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requests/{month}")
    public ResponseEntity<?> requestsByMonth(@PathVariable YearMonth month) {
        return ResponseEntity.ok(ApiResponseDto.builder().data(reportsService.getTtsRequestsReports(month)).build());
    }

}