package com.tts.transform.services;

import java.util.UUID;

import com.tts.transform.dto.SynthesizeRequest;

public interface SpeechService {

    byte[] synthesize(SynthesizeRequest request);

    byte[] download(UUID speechId);

}