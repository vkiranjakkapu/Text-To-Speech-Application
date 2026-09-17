package com.tts.transform.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.services.SpeechHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final SpeechHistoryService historyService;

    @GetMapping("/")
    public ResponseEntity<ApiResponseDto> getMySpeechHistory() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(historyService.getMyHistory()).build());
    }

}
