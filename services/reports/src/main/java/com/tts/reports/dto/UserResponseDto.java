package com.tts.reports.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tts.reports.enums.RoleType;
import com.tts.reports.enums.UserGender;
import com.tts.reports.models.Address;

import lombok.Builder;

@Builder
public record UserResponseDto(UUID id,
		String email,
		String firstName,
		String lastName,
		String phone,
		LocalDate dob,
		UserGender gender,
		Address address,
		boolean enabled,
		@JsonIgnore Set<RoleType> roles,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {
}