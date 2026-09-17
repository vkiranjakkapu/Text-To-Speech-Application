package com.tts.transform.services;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.tts.transform.dto.UserResponseDto;

public interface IdentityService {

    List<UserResponseDto> getAllUsers();

    Map<UUID, UserResponseDto> getAllUsersByIds(Set<UUID> userIds);

}