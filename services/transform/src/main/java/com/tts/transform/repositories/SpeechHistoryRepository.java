package com.tts.transform.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tts.transform.models.SpeechHistory;

public interface SpeechHistoryRepository extends JpaRepository<SpeechHistory, UUID> {

    List<SpeechHistory> findAllByOwnerId(UUID userId);

}
