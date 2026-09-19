package com.tts.transform.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    String extractText(MultipartFile file);

    List<String> getAllowedExtensions();

}