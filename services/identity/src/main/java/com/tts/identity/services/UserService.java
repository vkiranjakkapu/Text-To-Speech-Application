package com.tts.identity.services;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.tts.identity.dto.CreateUserRequestDto;
import com.tts.identity.dto.PasswordChangeRequestDto;
import com.tts.identity.dto.RegistrationRequest;
import com.tts.identity.dto.UpdateUserRequest;
import com.tts.identity.dto.UserResponse;
import com.tts.identity.entities.RoleType;

public interface UserService {
    UserResponse createUser(CreateUserRequestDto request);

    List<UserResponse> getAllUsers();

    List<UserResponse> getAllUsersByRole(RoleType role);

    List<UserResponse> getAllUsersWithIds(Collection<UUID> ids);

    UserResponse getUserById(UUID id);

    UserResponse getUserByEmail(String email);

    UserResponse register(RegistrationRequest request);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    UserResponse changePassword(PasswordChangeRequestDto request);

    void deleteUser(UUID id);
}