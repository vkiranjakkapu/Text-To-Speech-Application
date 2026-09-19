package com.tts.transform.services.imp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.tts.transform.dto.SupportedDocumentType;
import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.services.DocumentService;

@Service
public class DocumentServiceImp implements DocumentService {

    List<SupportedDocumentType> supportedTypes = List.of(
            new SupportedDocumentType("application/pdf", "PDF", ".pdf"),
            new SupportedDocumentType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "Word Document",
                    ".docx"),
            new SupportedDocumentType("application/msword", "Word Document", ".doc"),
            new SupportedDocumentType("text/plain", "Text File", ".txt"),
            new SupportedDocumentType("application/rtf", "Rich Text Format", ".rtf"),
            new SupportedDocumentType(
                    "application/vnd.oasis.opendocument.text",
                    "OpenDocument Text",
                    ".odt"));

    private final DataSize maxAllowedSize;
    private final Tika tika;

    public DocumentServiceImp(
            @Value("${spring.servlet.multipart.max-file-size}") DataSize maxAllowedSize) {

        this.tika = new Tika();
        this.maxAllowedSize = maxAllowedSize;
    }

    @Override
    public String extractText(MultipartFile file) {

        if (file.isEmpty()) {
            throw new BusinessException(
                    BusinessExceptions.INVALID_DOCUMENT,
                    "Document cannot be empty.");
        }

        if (file.getSize() > maxAllowedSize.toBytes()) {
            throw new BusinessException(
                    BusinessExceptions.DOCUMENT_TOO_LARGE,
                    "Document size must not exceed " + maxAllowedSize + ".");
        }

        try (InputStream inputStream = file.getInputStream()) {

            String detectedType = tika.detect(inputStream);

            if (!supportedTypes.stream().map(SupportedDocumentType::mimeType).toList().contains(detectedType)) {
                throw new BusinessException(
                        BusinessExceptions.INVALID_DOCUMENT,
                        "Unsupported document type.");
            }

        } catch (IOException e) {
            throw new BusinessException(
                    BusinessExceptions.DOCUMENT_EXTRACTION_ERROR,
                    "Unable to read document.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            return tika.parseToString(inputStream).replaceAll("\\R+", " ").trim();
        } catch (IOException | TikaException e) {
            throw new BusinessException(
                    BusinessExceptions.DOCUMENT_EXTRACTION_ERROR,
                    "Unable to extract text from document.");
        }
    }

    @Override
    public List<String> getAllowedExtensions() {
        return supportedTypes.stream().map(SupportedDocumentType::extension).toList();
    }
}