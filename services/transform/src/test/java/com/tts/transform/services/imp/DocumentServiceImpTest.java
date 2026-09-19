package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.exceptions.BusinessException;

class DocumentServiceImpTest {

    private DocumentServiceImp service;

    @BeforeEach
    void setUp() {
        service = new DocumentServiceImp(DataSize.ofBytes(1024));
    }

    @Test
    void extractText_shouldExtractTextFromValidTextFile() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello\n\nWorld".getBytes());

        String result = service.extractText(file);

        assertEquals("Hello World", result);
    }

    @Test
    void extractText_shouldRejectEmptyFile() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[0]);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.extractText(file));

        assertEquals(
                BusinessExceptions.INVALID_DOCUMENT,
                exception.getDefinition());
    }

    @Test
    void extractText_shouldRejectFileExceedingMaximumSize() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "large.txt",
                "text/plain",
                new byte[1025]);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.extractText(file));

        assertEquals(
                BusinessExceptions.DOCUMENT_TOO_LARGE,
                exception.getDefinition());
    }

    @Test
    void extractText_shouldRejectUnsupportedDocumentType() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.bin",
                "application/octet-stream",
                new byte[] {
                        0x00, 0x01, 0x02, 0x03
                });

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.extractText(file));

        assertEquals(
                BusinessExceptions.INVALID_DOCUMENT,
                exception.getDefinition());
    }

    @Test
    void extractText_shouldThrowWhenInputStreamCannotBeRead() throws Exception {

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(100L);
        when(file.getInputStream())
                .thenThrow(new IOException("Unable to read file"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.extractText(file));

        assertEquals(
                BusinessExceptions.DOCUMENT_EXTRACTION_ERROR,
                exception.getDefinition());
    }

    @Test
    void getAllowedExtensions_shouldReturnSupportedExtensions() {

        List<String> extensions = service.getAllowedExtensions();

        assertEquals(
                List.of(".pdf", ".docx", ".doc", ".txt", ".rtf", ".odt"),
                extensions);
    }
}