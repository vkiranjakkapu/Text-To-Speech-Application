package com.tts.transform.services.imp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.exceptions.BusinessException;

@Service
public class DocumentServiceImp {
    private final Tika tika = new Tika();

    public String extractText(MultipartFile file) {

        try (InputStream inputStream = file.getInputStream()) {

            return tika.parseToString(inputStream);

        } catch (IOException | TikaException e) {
            throw new BusinessException(
                    BusinessExceptions.DOCUMENT_EXTRACTION_ERROR,
                    "Unable to extract text from document.", e);
        }
    }
}
