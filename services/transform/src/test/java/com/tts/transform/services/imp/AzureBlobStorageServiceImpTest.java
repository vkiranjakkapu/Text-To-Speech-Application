package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import com.azure.core.util.BinaryData;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.tts.transform.properties.AzureStorageProperties;

class AzureBlobStorageServiceImpTest {

	private AzureStorageProperties properties;

	private BlobContainerClient containerClient;

	private BlobClient blobClient;

	private AzureBlobStorageServiceImp service;

	@BeforeEach
	void setUp() {

		properties = mock(AzureStorageProperties.class);

		when(properties.connectionString())
				.thenReturn("test-connection-string");

		when(properties.containerName())
				.thenReturn("test-container");

		containerClient = mock(BlobContainerClient.class);

		blobClient = mock(BlobClient.class);

		try (MockedConstruction<BlobContainerClientBuilder> _ = Mockito.mockConstruction(
				BlobContainerClientBuilder.class,
				(builder, context) -> {

					doReturn(builder)
							.when(builder)
							.connectionString("test-connection-string");

					doReturn(builder)
							.when(builder)
							.containerName("test-container");

					doReturn(containerClient)
							.when(builder)
							.buildClient();
				})) {

			service = new AzureBlobStorageServiceImp(properties);
		}
	}

	@Test
	void constructor_shouldCreateContainerIfNotExists() {

		verify(containerClient)
				.createIfNotExists();
	}

	@Test
	void upload_shouldUploadAudioAndReturnBlobName() {

		byte[] audio = "audio-data".getBytes();
		String fileName = "speech.mp3";

		when(containerClient.getBlobClient(any(String.class)))
				.thenReturn(blobClient);

		String result = service.upload(audio, fileName);

		assertEquals(true, result.endsWith("-" + fileName));

		verify(containerClient)
				.getBlobClient(result);

		verify(blobClient)
				.upload(
						any(InputStream.class),
						eq((long) audio.length),
						eq(true));
	}

	@Test
	void download_shouldReturnBlobContent() {

		String filePath = "audio/speech.mp3";

		byte[] expected = "audio-data".getBytes();

		BinaryData binaryData = BinaryData.fromBytes(expected);

		when(containerClient.getBlobClient(filePath))
				.thenReturn(blobClient);

		when(blobClient.downloadContent())
				.thenReturn(binaryData);

		byte[] result = service.download(filePath);

		assertArrayEquals(expected, result);

		verify(containerClient)
				.getBlobClient(filePath);

		verify(blobClient)
				.downloadContent();
	}

	@Test
	void delete_shouldDeleteBlobIfExists() {

		String filePath = "audio/speech.mp3";

		when(containerClient.getBlobClient(filePath))
				.thenReturn(blobClient);

		service.delete(filePath);

		verify(containerClient)
				.getBlobClient(filePath);

		verify(blobClient)
				.deleteIfExists();
	}
}