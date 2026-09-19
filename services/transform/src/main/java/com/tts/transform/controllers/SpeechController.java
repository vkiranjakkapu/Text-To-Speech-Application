package com.tts.transform.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.web.model.ErrorResponse;
import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.dto.SynthesizeRequest;
import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.enums.SynthesisType;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.properties.DefaultProperties;
import com.tts.transform.services.SpeechService;
import com.tts.transform.services.TtsProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1")
@RequiredArgsConstructor
public class SpeechController {

    private final TtsProvider ttsProvider;
    private final SpeechService speechService;
    private final DefaultProperties properties;

    @Operation(summary = "Get available voices")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voices retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "TTS service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/voices")
    public ResponseEntity<ApiResponseDto> getVoices() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(ttsProvider.getVoices()).build());
    }

    @Operation(summary = "Download speech audio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Speech audio downloaded successfully", content = @Content(mediaType = "audio/mpeg", schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Speech history not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{speechId}/download")
    public ResponseEntity<byte[]> downloadSpeech(@PathVariable UUID speechId) {
        return ResponseEntity.ok(speechService.download(speechId));
    }

    @Operation(summary = "Synthesize text to speech")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Speech synthesized successfully", content = @Content(mediaType = "audio/mpeg", schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "400", description = "Invalid synthesis request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "411", description = "Text length is below the minimum requirement", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "413", description = "Text length exceeds the allowed limit", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Monthly usage limit exceeded", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "TTS service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/synthesize")
    public ResponseEntity<?> synthesize(@Valid @RequestBody SynthesizeRequest request) {
        String text = request.text().replaceAll("\\R+", " ").trim();

        if (text.length() < properties.getRequest().getMinTextLength()) {
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

        if (text.length() > maxLength) {
            throw new BusinessException(BusinessExceptions.MAX_LENGTH_EXCEEDED,
                    "Text for Synthesis can't exceed the length of " + properties.getRequest().getMaxTextLength()
                            + " characters.",
                    HttpStatus.CONTENT_TOO_LARGE);
        }

        byte[] synthesize = speechService.synthesize(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .body(synthesize);
    }

}
