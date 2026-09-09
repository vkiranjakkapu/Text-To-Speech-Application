package com.tts.identity.services;

import com.tts.identity.dto.LoginRequestDto;
import com.tts.identity.dto.LoginResponseDto;
import com.tts.identity.dto.LogoutRequestDto;
import com.tts.identity.dto.RefreshTokenRequest;
import com.tts.identity.dto.RefreshTokenResponse;

public interface AuthenticationService {

    LoginResponseDto login(LoginRequestDto request);

    RefreshTokenResponse refresh(RefreshTokenRequest request);

    void logout(LogoutRequestDto request);

}