package com.tts.transform.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.services.CurrentUserService;
import com.tts.transform.services.SpeechHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final SpeechHistoryService historyService;
    private final CurrentUserService currentUser;

    @GetMapping("/")
    public ResponseEntity<ApiResponseDto> getMySpeechHistory() {
        return ResponseEntity.ok(ApiResponseDto.builder()
                .data(currentUser.isAdmin() ? historyService.getAllHistoryRecordsMap() : historyService.getMyHistory())
                .build());
    }

    @DeleteMapping("/{speechId}")
    public ResponseEntity<ApiResponseDto> deleteHistory(@PathVariable UUID speechId) {
        return ResponseEntity.ok().body(ApiResponseDto.builder()
                .data(historyService.deleteHistoryRecord(speechId))
                .build());
    }

}
