package com.tts.transform.controllers;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.web.model.ErrorResponse;
import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.services.CurrentUserService;
import com.tts.transform.services.SpeechHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final SpeechHistoryService historyService;
    private final CurrentUserService currentUser;

    @Operation(summary = "Get speech history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Speech history retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/")
    public ResponseEntity<ApiResponseDto> getMySpeechHistory() {
        return ResponseEntity.ok(ApiResponseDto.builder()
                .data(currentUser.isAdmin() ? historyService.getAllHistoryRecordsMap() : historyService.getMyHistory())
                .build());
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Speech history deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Speech history not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{speechId}")
    public ResponseEntity<ApiResponseDto> deleteHistory(@PathVariable UUID speechId) {
        return ResponseEntity.ok().body(ApiResponseDto.builder()
                .data(historyService.deleteHistoryRecord(speechId))
                .build());
    }

}
