package com.tts.transform.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tts.transform.dto.ApiResponse;
import com.tts.transform.services.imp.SpeechServiceImp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final SpeechServiceImp speechService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getMySpeechHistory() {
        return ResponseEntity.ok(ApiResponse.builder().body(speechService.getMyHistory()).build());
    }

}
