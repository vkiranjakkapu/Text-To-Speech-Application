package com.tts.transform.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.web.model.ErrorResponse;
import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.dto.DocumentSpeechResponseDto;
import com.tts.transform.dto.SynthesizeRequest;
import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.enums.ResponseStatus;
import com.tts.transform.enums.SynthesisType;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.properties.DefaultProperties;
import com.tts.transform.services.SpeechService;
import com.tts.transform.services.TtsProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1")
@RequiredArgsConstructor
public class SpeechController {

    private final TtsProvider ttsProvider;
    private final SpeechService speechService;
    private final DefaultProperties properties;

    @GetMapping("/voices")
    public ResponseEntity<ApiResponseDto> getVoices() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(ttsProvider.getVoices()).build());
    }

    @GetMapping("/{speechId}/download")
    public ResponseEntity<byte[]> downloadSpeech(@PathVariable UUID speechId) {
        return ResponseEntity.ok(speechService.download(speechId));
    }

    @PostMapping("/synthesize")
    public ResponseEntity<?> synthesize(@Valid @RequestBody SynthesizeRequest request) {

        if (request.text().length() < properties.getRequest().getMinTextLength()) {
            throw new BusinessException(BusinessExceptions.MIN_LENGTH_REQUIRED,
                    "Text for Synthesis should atleast be " + properties.getRequest().getMinTextLength()
                            + " characters length.",
                    HttpStatus.LENGTH_REQUIRED);
        }

        long maxLength = Optional.ofNullable(request.type()).map(type -> {
            if (type.equals(SynthesisType.DOCUMENT)) {
                return properties.getRequest().getMaxDoctextLength();
            } else {
                return properties.getRequest().getMaxTextLength();
            }
        }).orElse(properties.getRequest().getMaxTextLength());

        if (request.text().length() > maxLength) {
            throw new BusinessException(BusinessExceptions.MAX_LENGTH_EXCEEDED,
                    "Text for Synthesis can't exceed the length of " + properties.getRequest().getMaxTextLength()
                            + " characters.",
                    HttpStatus.CONTENT_TOO_LARGE);
        }

        try {
            byte[] synthesize = speechService.synthesize(request);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                    .body(synthesize);
        } catch (BusinessException e) {
            if (e.getDefinition().equals(BusinessExceptions.LIMIT_EXCEEDED)) {
                return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(ApiResponseDto.builder()
                        .status(ResponseStatus.ERROR)
                        .data(DocumentSpeechResponseDto.builder()
                                .error(new ErrorResponse(e.getDefinition(), e.getMessage()))
                                .suggestion("You can use our AI Service to reduce the text size.")
                                .text(request.text())
                                .build())
                        .build());
            }
            throw e;
        }
    }

}
