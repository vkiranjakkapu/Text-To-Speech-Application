package com.tts.transform.dto;

import java.time.LocalDateTime;

import com.tts.transform.enums.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestResponseDto<T> {

    @Builder.Default()
    private ResponseStatus status = ResponseStatus.SUCCESS;

    private T data;

    @Builder.Default()
    private LocalDateTime timestamp = LocalDateTime.now();
}