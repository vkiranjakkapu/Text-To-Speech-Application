package com.tts.transform.services.imp;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
import com.tts.transform.services.SpeechHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpeechHistoryServiceImp implements SpeechHistoryService {

    private final SpeechHistoryRepository historyRepository;
    private final CurrentUserService currentUser;
    private final IdentityService identityService;
    private final AzureBlobStorageService storageService;

    @Override
    public SpeechHistory createRecord(SpeechHistory speechHistory) {
        return historyRepository.save(speechHistory);
    }

    @Override
    public SpeechHistory getHistoryById(UUID id) {
        return historyRepository.findById(id).orElseThrow(
                () -> new BusinessException(BusinessExceptions.RESOURCE_NOT_FOUND, "record with given id not found."));
    }

    @Override
    public List<SpeechHistory> getMyHistory() {
        return historyRepository.findAllByOwnerIdAndIsDeletedFalse(currentUser.userId());
    }

    @Override
    public List<SpeechHistory> getAllHistoryRecords() {
        return historyRepository.findAll();
    }

    @Override
    public List<SpeechHistoryDto> getAllHistoryRecordsMap() {
        List<SpeechHistory> allRecords = historyRepository.findAllByIsDeletedFalse();
        Set<UUID> allUserIds = allRecords.stream().map(rec -> rec.getOwnerId()).collect(Collectors.toSet());
        Map<UUID, UserResponseDto> allUsers = identityService.getAllUsersByIds(allUserIds);
        return allRecords.stream().map(rec -> mapToResponse(rec, allUsers.get(rec.getOwnerId()))).toList();
    }

    @Override
    public boolean deleteHistoryRecord(UUID histId) {
        SpeechHistory record = getHistoryById(histId);
        if (!record.getOwnerId().equals(currentUser.userId())) {
            throw new SecurityException(SecurityExceptions.FORBIDDEN_ACCESS,
                    "You are not allowed to perform this operation.");
        }
        record.setDeleted(true);
        storageService.delete(record.getAudioPath());
        historyRepository.save(record);
        return true;
    }

    private SpeechHistoryDto mapToResponse(SpeechHistory history, UserResponseDto user) {
        return SpeechHistoryDto.builder()
                .id(history.getId())
                .user(user)
                .text(history.getText())
                .language(history.getLanguage())
                .voice(history.getVoice())
                .audioPath(history.getAudioPath())
                .createdAt(history.getCreatedAt())
                .build();
    }

}
