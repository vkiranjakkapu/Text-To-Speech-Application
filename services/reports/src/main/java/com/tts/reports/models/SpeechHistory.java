package com.tts.reports.models;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpeechHistory {

    private UUID id;

    private UUID ownerId;

    private String text;

    private String language;

    private String voiceName;

    private String voice;

    private String audioPath;

    private LocalDateTime createdAt;

}
