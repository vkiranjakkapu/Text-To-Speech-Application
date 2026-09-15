package com.tts.transform.services;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    String extractText(MultipartFile file);

}