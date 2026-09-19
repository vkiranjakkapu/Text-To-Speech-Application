package com.tts.reports.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.platform.web.exception.WebExceptions;
import com.platform.web.model.ErrorResponse;
import com.tts.reports.exceptions.BusinessException;
import com.tts.reports.exceptions.InternalCommunicationException;
import com.tts.reports.exceptions.SecurityException;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Test
	void handleSecurityException_shouldReturnForbidden() {

		SecurityException exception = new SecurityException("Access denied");

		ResponseEntity<ErrorResponse> response = handler.handleSecurityException(exception);

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	void handleBusinessException_shouldReturnBadRequest() {

		BusinessException exception = new BusinessException("Invalid request");

		ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	void handleBusinessException_shouldUseExceptionStatus() {

		BusinessException exception = new BusinessException(
				WebExceptions.RESOURCE_NOT_FOUND,
				"Resource not found",
				HttpStatus.BAD_REQUEST);

		ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);

		assertEquals(
				HttpStatus.BAD_REQUEST,
				response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	void handleInternalCommunicationExceptions_shouldReturnExceptionStatus() {

		InternalCommunicationException exception = new InternalCommunicationException(
				"Identity service unavailable",
				HttpStatus.SERVICE_UNAVAILABLE);

		ResponseEntity<?> response = handler.handleInternalCommunicationExceptions(exception);

		assertEquals(
				HttpStatus.SERVICE_UNAVAILABLE,
				response.getStatusCode());

		assertEquals(
				MediaType.APPLICATION_JSON,
				response.getHeaders().getContentType());

		assertEquals(
				"Identity service unavailable",
				response.getBody());
	}

	@Test
	void handleUnexpectedExceptions_shouldReturnInternalServerError() {

		Exception exception = new RuntimeException("Unexpected failure");

		ResponseEntity<ErrorResponse> response = handler.handleUnexpectedExceptions(exception);

		assertEquals(
				HttpStatus.INTERNAL_SERVER_ERROR,
				response.getStatusCode());

		assertNotNull(response.getBody());
	}
}