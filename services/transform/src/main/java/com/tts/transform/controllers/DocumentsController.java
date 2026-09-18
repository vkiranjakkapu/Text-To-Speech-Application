package com.tts.transform.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.services.DocumentService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/speech/api/v1/documents")
@RequiredArgsConstructor
public class DocumentsController {

    private final DocumentService documentService;

    @GetMapping("/support")
    public ResponseEntity<ApiResponseDto> getSupportedDocumentTypes() {
        return ResponseEntity.ok(ApiResponseDto.builder().data(documentService.getAllowedExtensions()).build());
    }

    @PostMapping("/extract")
    public ResponseEntity<ApiResponseDto> extractText(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(ApiResponseDto.builder().data(documentService.extractText(file)).build());
    }

}