package com.tts.transform.services;

import com.tts.transform.dto.SynthesizeRequest;

public interface SpeechService {

    byte[] synthesize(SynthesizeRequest request);

}