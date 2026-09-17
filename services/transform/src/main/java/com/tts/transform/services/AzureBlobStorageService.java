package com.tts.transform.services;

public interface AzureBlobStorageService {

    String upload(byte[] audio, String fileName);

    byte[] download(String filePath);

    void delete(String filePath);

}