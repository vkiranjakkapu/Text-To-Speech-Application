package com.tts.transform.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tts.transform.services.DocumentService;

class DocumentsControllerTest {

	private MockMvc mockMvc;

	@Mock
	private DocumentService documentService;

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);

		mockMvc = MockMvcBuilders
				.standaloneSetup(new DocumentsController(documentService))
				.build();
	}

	@Test
	void getSupportedDocumentTypes_shouldReturnExtensions() throws Exception {

		when(documentService.getAllowedExtensions())
				.thenReturn(List.of(".pdf", ".docx", ".doc", ".txt", ".rtf", ".odt"));

		mockMvc.perform(get("/speech/api/v1/documents/support"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json("""
						{
						    "status": "SUCCESS",
						    "data": [".pdf", ".docx", ".doc", ".txt", ".rtf", ".odt"]
						}
						"""));
	}

	@Test
	void extractText_shouldReturnExtractedText() throws Exception {

		MockMultipartFile file = new MockMultipartFile(
				"file",
				"test.txt",
				MediaType.TEXT_PLAIN_VALUE,
				"Hello World".getBytes());

		when(documentService.extractText(org.mockito.ArgumentMatchers.any()))
				.thenReturn("Hello World");

		mockMvc.perform(
				multipart("/speech/api/v1/documents/extract")
						.file(file))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json("""
						{
						    "status": "SUCCESS",
						    "data": "Hello World"
						}
						"""));
	}

	@Test
	void extractText_shouldAcceptMultipartFile() throws Exception {

		MockMultipartFile file = new MockMultipartFile(
				"file",
				"document.pdf",
				MediaType.APPLICATION_PDF_VALUE,
				"PDF content".getBytes());

		when(documentService.extractText(org.mockito.ArgumentMatchers.any()))
				.thenReturn("Extracted PDF text");

		mockMvc.perform(
				multipart("/speech/api/v1/documents/extract")
						.file(file))
				.andExpect(status().isOk())
				.andExpect(content().json("""
						{
						    "status": "SUCCESS",
						    "data": "Extracted PDF text"
						}
						"""));
	}

	@Test
	void extractText_shouldRejectMissingFile() throws Exception {

		mockMvc.perform(
				multipart("/speech/api/v1/documents/extract"))
				.andExpect(status().isBadRequest());
	}
}