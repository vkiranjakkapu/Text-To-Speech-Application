package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.platform.web.exception.SecurityExceptions;
import com.tts.transform.dto.SpeechHistoryDto;
import com.tts.transform.dto.UserResponseDto;
import com.tts.transform.enums.BusinessExceptions;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.exceptions.SecurityException;
import com.tts.transform.models.SpeechHistory;
import com.tts.transform.repositories.SpeechHistoryRepository;
import com.tts.transform.services.AzureBlobStorageService;
import com.tts.transform.services.CurrentUserService;
import com.tts.transform.services.IdentityService;

@ExtendWith(MockitoExtension.class)
class SpeechHistoryServiceImpTest {

    @Mock
    private SpeechHistoryRepository historyRepository;

    @Mock
    private CurrentUserService currentUser;

    @Mock
    private IdentityService identityService;

    @Mock
    private AzureBlobStorageService storageService;

    @InjectMocks
    private SpeechHistoryServiceImp historyService;

    private UUID userId;
    private UUID historyId;
    private SpeechHistory history;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        historyId = UUID.randomUUID();

        history = SpeechHistory.builder()
                .id(historyId)
                .ownerId(userId)
                .text("Hello world")
                .language("en-US")
                .voice("en-US-JennyNeural")
                .audioPath("speech/test.mp3")
                .build();
    }

    @Test
    void createRecord_shouldSaveAndReturnRecord() {
        when(historyRepository.save(history)).thenReturn(history);

        SpeechHistory result = historyService.createRecord(history);

        assertSame(history, result);

        verify(historyRepository).save(history);
    }

    @Test
    void getHistoryById_shouldReturnRecordWhenFound() {
        when(historyRepository.findById(historyId))
                .thenReturn(Optional.of(history));

        SpeechHistory result = historyService.getHistoryById(historyId);

        assertSame(history, result);

        verify(historyRepository).findById(historyId);
    }

    @Test
    void getHistoryById_shouldThrowWhenRecordDoesNotExist() {
        when(historyRepository.findById(historyId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> historyService.getHistoryById(historyId));

        assertEquals(
                BusinessExceptions.RESOURCE_NOT_FOUND,
                exception.getDefinition());

        verify(historyRepository).findById(historyId);
    }

    @Test
    void getMyHistory_shouldReturnCurrentUsersHistory() {
        List<SpeechHistory> records = List.of(history);

        when(currentUser.userId()).thenReturn(userId);
        when(historyRepository
                .findAllByOwnerIdAndIsDeletedFalseOrderByCreatedAtDesc(userId))
                .thenReturn(records);

        List<SpeechHistory> result = historyService.getMyHistory();

        assertSame(records, result);

        verify(currentUser).userId();
        verify(historyRepository)
                .findAllByOwnerIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }

    @Test
    void getAllHistoryRecords_shouldReturnAllRecords() {
        List<SpeechHistory> records = List.of(history);

        when(historyRepository.findAll()).thenReturn(records);

        List<SpeechHistory> result = historyService.getAllHistoryRecords();

        assertSame(records, result);

        verify(historyRepository).findAll();
    }

    @Test
    void getAllHistoryRecordsMap_shouldMapRecordsWithUsers() {
        UserResponseDto user = UserResponseDto.builder()
                .id(userId)
                .build();

        List<SpeechHistory> records = List.of(history);

        when(historyRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc())
                .thenReturn(records);

        when(identityService.getAllUsersByIds(Set.of(userId)))
                .thenReturn(Map.of(userId, user));

        List<SpeechHistoryDto> result =
                historyService.getAllHistoryRecordsMap();

        assertEquals(1, result.size());

        SpeechHistoryDto dto = result.get(0);

        assertEquals(history.getId(), dto.getId());
        assertSame(user, dto.getUser());
        assertEquals(history.getText(), dto.getText());
        assertEquals(history.getLanguage(), dto.getLanguage());
        assertEquals(history.getVoice(), dto.getVoice());
        assertEquals(history.getAudioPath(), dto.getAudioPath());
        assertEquals(history.getCreatedAt(), dto.getCreatedAt());

        verify(historyRepository)
                .findAllByIsDeletedFalseOrderByCreatedAtDesc();

        verify(identityService)
                .getAllUsersByIds(Set.of(userId));
    }

    @Test
    void deleteHistoryRecord_shouldDeleteRecordAndAudioWhenOwnerMatches() {
        when(historyRepository.findById(historyId))
                .thenReturn(Optional.of(history));

        when(currentUser.userId()).thenReturn(userId);

        when(historyRepository.save(history))
                .thenReturn(history);

        boolean result = historyService.deleteHistoryRecord(historyId);

        assertTrue(result);
        assertTrue(history.isDeleted());

        verify(historyRepository).findById(historyId);
        verify(currentUser).userId();
        verify(storageService).delete(history.getAudioPath());
        verify(historyRepository).save(history);
    }

    @Test
    void deleteHistoryRecord_shouldThrowForbiddenWhenUserIsNotOwner() {
        UUID anotherUserId = UUID.randomUUID();

        when(historyRepository.findById(historyId))
                .thenReturn(Optional.of(history));

        when(currentUser.userId()).thenReturn(anotherUserId);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> historyService.deleteHistoryRecord(historyId));

        assertEquals(
                SecurityExceptions.FORBIDDEN_ACCESS,
                exception.getDefinition());

        assertFalse(history.isDeleted());

        verify(historyRepository).findById(historyId);
        verify(currentUser).userId();
        verify(storageService, never()).delete(any());
        verify(historyRepository, never()).save(any());
    }
}