package com.tts.transform.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.web.model.ErrorResponse;
import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.services.DocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/speech/api/v1/documents")
@RequiredArgsConstructor
public class DocumentsController {

    private final DocumentService documentService;

    @Operation(summary = "Get supported document types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supported document types retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/support")
    public ResponseEntity<ApiResponseDto> getSupportedDocumentTypes() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(documentService.getAllowedExtensions()).build());
    }

    @Operation(summary = "Extract text from document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Text extracted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or unsupported document", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/extract")
    public ResponseEntity<ApiResponseDto> extractText(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(ApiResponseDto.builder().data(documentService.extractText(file)).build());
    }

}