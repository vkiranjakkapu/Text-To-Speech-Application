package com.tts.transform.services;

import java.util.List;

import com.tts.transform.dto.VoiceDto;

public interface TtsProvider {

    List<VoiceDto> getVoices();

    byte[] synthesize(
            String text,
            String language,
            String voice,
            String style,
            String rate,
            String pitch,
            String volume);

}
