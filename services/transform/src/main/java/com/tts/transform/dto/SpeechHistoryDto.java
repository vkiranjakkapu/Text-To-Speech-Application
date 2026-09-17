package com.tts.transform.dto;

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
public class SpeechHistoryDto {

    private UUID id;

    private UserResponseDto user;

    private String text;

    private String language;

    private String voice;

    private String audioPath;

    private LocalDateTime createdAt;

}
