package com.tts.transform.services;

public interface AzureAiService {

    String enhanceText(String text);

    String resizeText(String text, long length);

    String summariseText(String text);

}