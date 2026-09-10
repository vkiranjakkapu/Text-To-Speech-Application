package com.tts.transform.services.imp;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.tts.transform.properties.AzureStorageProperties;

@Service
public class AzureBlobStorageService {

    private final BlobContainerClient containerClient;

    public AzureBlobStorageService(AzureStorageProperties properties) {

        this.containerClient = new BlobContainerClientBuilder()
                .connectionString(properties.connectionString())
                .containerName(properties.containerName())
                .buildClient();

        containerClient.createIfNotExists();
    }

    public String upload(byte[] audio, String fileName) {

        String blobName = UUID.randomUUID() + "-" + fileName;

        BlobClient blobClient = containerClient.getBlobClient(blobName);

        blobClient.upload(
                new ByteArrayInputStream(audio),
                audio.length,
                true);

        return blobName;
    }

    public byte[] download(String filePath) {

        BlobClient blobClient = containerClient.getBlobClient(filePath);

        return blobClient.downloadContent().toBytes();
    }
}