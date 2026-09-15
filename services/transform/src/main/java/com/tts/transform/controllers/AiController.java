package com.tts.transform.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.dto.EnhanceTextRequest;
import com.tts.transform.enums.EnhancementType;
import com.tts.transform.exceptions.ValidationException;
import com.tts.transform.services.AzureAiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AzureAiService aiService;

    @PostMapping("/enhance")
    public ResponseEntity<ApiResponseDto> enhance(
            @Valid @RequestBody EnhanceTextRequest request) {

        EnhancementType type = Optional.ofNullable(request.type()).map(reqType -> {
            return reqType;
        }).orElse(EnhancementType.ENHANCE);

        String result;

        switch (type) {
            case REDUCE:
                requireLength(request.length());
                result = aiService.resizeText(request.text(), request.length());
                break;

            case SUMMARISE:
                result = aiService.summariseText(request.text());
                break;

            default:
                result = aiService.enhanceText(request.text());
                break;
        }

        return ResponseEntity.ok(ApiResponseDto.builder().body(result).build());
    }

    private void requireLength(Long length) {
        if (length == null) {
            throw new ValidationException("length", length, "length is required to reduce the text!");
        }
    }

}
