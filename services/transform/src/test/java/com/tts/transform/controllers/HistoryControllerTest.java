package com.tts.transform.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.tts.transform.dto.ApiResponseDto;
import com.tts.transform.dto.SpeechHistoryDto;
import com.tts.transform.models.SpeechHistory;
import com.tts.transform.services.CurrentUserService;
import com.tts.transform.services.SpeechHistoryService;

class HistoryControllerTest {

	private SpeechHistoryService historyService;

	private CurrentUserService currentUser;

	private HistoryController controller;

	@BeforeEach
	void setUp() {

		historyService = mock(SpeechHistoryService.class);

		currentUser = mock(CurrentUserService.class);

		controller = new HistoryController(
				historyService,
				currentUser);
	}

	@Test
	void getMySpeechHistory_shouldReturnMyHistoryForRegularUser() {

		List<SpeechHistory> history = List.of();

		when(currentUser.isAdmin())
				.thenReturn(false);

		when(historyService.getMyHistory())
				.thenReturn(history);

		ResponseEntity<ApiResponseDto> response = controller.getMySpeechHistory();

		assertEquals(
				history,
				response.getBody().data());

		verify(currentUser).isAdmin();

		verify(historyService)
				.getMyHistory();
	}

	@Test
	void getMySpeechHistory_shouldReturnAllHistoryMapForAdmin() {

		List<SpeechHistoryDto> history = List.of(mock(SpeechHistoryDto.class));

		when(currentUser.isAdmin())
				.thenReturn(true);

		when(historyService.getAllHistoryRecordsMap())
				.thenReturn(history);

		ResponseEntity<ApiResponseDto> response = controller.getMySpeechHistory();

		assertEquals(
				history,
				response.getBody().data());

		verify(currentUser).isAdmin();

		verify(historyService)
				.getAllHistoryRecordsMap();
	}

	@Test
	void deleteHistory_shouldDeleteHistoryRecord() {

		UUID speechId = UUID.randomUUID();

		when(historyService.deleteHistoryRecord(speechId))
				.thenReturn(true);

		ResponseEntity<ApiResponseDto> response = controller.deleteHistory(speechId);

		assertEquals(
				true,
				response.getBody().data());

		verify(historyService)
				.deleteHistoryRecord(speechId);
	}

	@Test
	void deleteHistory_shouldReturnFalseWhenHistoryRecordWasNotDeleted() {

		UUID speechId = UUID.randomUUID();

		when(historyService.deleteHistoryRecord(speechId))
				.thenReturn(false);

		ResponseEntity<ApiResponseDto> response = controller.deleteHistory(speechId);

		assertEquals(
				false,
				response.getBody().data());

		verify(historyService)
				.deleteHistoryRecord(speechId);
	}
}