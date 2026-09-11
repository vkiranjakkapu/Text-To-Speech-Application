package com.tts.transform.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tts.transform.dto.ApiResponse;
import com.tts.transform.dto.SynthesizeRequest;
import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.properties.DefaultProperties;
import com.tts.transform.services.TtsProvider;
import com.tts.transform.services.imp.SpeechServiceImp;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1")
@RequiredArgsConstructor
public class SpeechController {

    private final TtsProvider ttsProvider;
    private final SpeechServiceImp speechService;
    private final DefaultProperties properties;

    @GetMapping("/voices")
    public ResponseEntity<ApiResponse> getVoices() {
        return ResponseEntity.ok(ApiResponse.builder().body(ttsProvider.getVoices()).build());
    }

    @PostMapping("/synthesize")
    public ResponseEntity<byte[]> synthesize(
            @RequestBody SynthesizeRequest request) {

        if (request.text().length() > properties.getRequest().getMaxCharLength()) {
            throw new BusinessException(BusinessExceptions.MAX_LENGTH_EXCEEDED,
                    "Input text can't exceed the length of " + properties.getRequest().getMaxCharLength()
                            + " characters.");
        }

        byte[] audio = speechService.synthesize(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .body(audio);
    }

}
