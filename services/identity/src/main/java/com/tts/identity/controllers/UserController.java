package com.tts.identity.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.security.context.AuthenticationContext;
import com.platform.security.model.AuthenticatedUser;
import com.tts.identity.dto.APIResponseDto;
import com.tts.identity.dto.CreateUserRequestDto;
import com.tts.identity.dto.FetchUsersRequestDto;
import com.tts.identity.dto.PasswordChangeRequestDto;
import com.tts.identity.dto.RegistrationRequest;
import com.tts.identity.dto.UpdateUserRequest;
import com.tts.identity.dto.UserResponse;
import com.tts.identity.entities.RoleType;
import com.tts.identity.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/identity/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationContext authContext;

    @Operation(summary = "Get Loggedin User")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/me")
    public ResponseEntity<APIResponseDto> me() {
        AuthenticatedUser user = authContext.getCurrentUser().orElse(null);
        return ResponseEntity
                .ok(APIResponseDto.builder().data(userService.getUserById(UUID.fromString(user.getUserId()))).build());
    }

    @Operation(summary = "Create User")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<APIResponseDto> createUser(
            @Valid @RequestBody CreateUserRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponseDto.builder().data(userService.createUser(request)).build());
    }

    @Operation(summary = "Registration By Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
    })
    @PostMapping("/register")
    public ResponseEntity<APIResponseDto> register(@Valid @ModelAttribute RegistrationRequest request) {
        return ResponseEntity.ok(APIResponseDto.builder().data(userService.register(request)).build());
    }

    @Operation(summary = "Get all users")
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<APIResponseDto> getAllUsers() {
        return ResponseEntity.ok(APIResponseDto.builder().data(userService.getAllUsers()).build());
    }

    @Operation(summary = "Get all users by role")
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<APIResponseDto> getAllUsersByRole(@PathVariable String role) {
        List<UserResponse> allUsers;
        if (role.equals(RoleType.USER.toString())) {
            allUsers = userService.getAllUsersByRole(RoleType.USER);
        } else {
            allUsers = userService.getAllUsersByRole(RoleType.ADMIN);
        }
        return ResponseEntity.ok(APIResponseDto.builder().data(allUsers).build());
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<APIResponseDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(APIResponseDto.builder().data(userService.getUserById(id)).build());
    }

    @Operation(summary = "Get all users with ids")
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<APIResponseDto> getAllUsersWithIds(@RequestBody FetchUsersRequestDto request) {
        return ResponseEntity
                .ok(APIResponseDto.builder().data(userService.getAllUsersWithIds(request.ids())).build());
    }

    @Operation(summary = "Update user")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<APIResponseDto> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(APIResponseDto.builder().data(userService.updateUser(id, request)).build());
    }

    @Operation(summary = "Change Password")
    @PatchMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<APIResponseDto> changePassword(
            @Valid @RequestBody PasswordChangeRequestDto request) {

        return ResponseEntity.ok(APIResponseDto.builder().data(userService.changePassword(request)).build());
    }

    @Operation(summary = "Soft delete user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponseDto> deleteUser(@PathVariable UUID id) {

        userService.deleteUser(id);
        HashMap<String, Boolean> body = new HashMap<>();
        body.put("status", true);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(APIResponseDto.builder().data(body).build());
    }
}
