package com.tts.identity.dto;

public record RefreshTokenResponse(
		String accessToken,
		String refreshToken) {
}
