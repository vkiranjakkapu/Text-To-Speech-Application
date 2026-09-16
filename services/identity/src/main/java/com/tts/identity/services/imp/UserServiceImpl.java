package com.tts.identity.services.imp;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.security.context.AuthenticationContext;
import com.platform.security.model.AuthenticatedUser;
import com.tts.identity.dto.CreateUserRequestDto;
import com.tts.identity.dto.PasswordChangeRequestDto;
import com.tts.identity.dto.RegistrationRequest;
import com.tts.identity.dto.UpdateUserRequest;
import com.tts.identity.dto.UserResponse;
import com.tts.identity.entities.Address;
import com.tts.identity.entities.Role;
import com.tts.identity.entities.RoleType;
import com.tts.identity.entities.User;
import com.tts.identity.enums.IdentityExceptions;
import com.tts.identity.exceptions.BusinessException;
import com.tts.identity.exceptions.EmailAlreadyUsedException;
import com.tts.identity.exceptions.ForbiddenException;
import com.tts.identity.exceptions.ResourceNotFoundException;
import com.tts.identity.repository.RoleRepository;
import com.tts.identity.repository.UserRepository;
import com.tts.identity.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationContext authenticationContext;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public UserResponse createUser(CreateUserRequestDto request) {

		AuthenticatedUser currentUser = authenticationContext.getCurrentUser().orElse(null);
		validatePermission(currentUser);

		if (userRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyUsedException("Email already exists");
		}

		Role role = roleRepository.findByName(request.role())
				.orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		User user = User.builder()
				.firstName(request.firstName())
				.lastName(request.lastName())
				.email(request.email())
				.password(passwordEncoder.encode(Optional.ofNullable(request.password()).orElse("password")))
				.dob(request.dob())
				.phone(request.phone())
				.gender(request.gender())
				.address(Address.builder()
						.street(request.address().street())
						.pinCode(request.address().pinCode())
						.state(request.address().state())
						.country(request.address().country())
						.build())
				.enabled(true)
				.roles(Set.of(role))
				.build();

		User saved = userRepository.save(user);

		return mapToResponse(saved, true);
	}

	private void validatePermission(AuthenticatedUser user) {
		if (user.getAuthorities().size() == 1
				&& user.getAuthorities().stream().toList().getFirst().equalsIgnoreCase("ROLE_USER"))
			throw new ForbiddenException("You are not allowed to create this user.");
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {

		return userRepository.findAllByDeletedFalse()
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Override
	public List<UserResponse> getAllUsersByRole(RoleType type) {
		Role role = roleRepository.findByName(type)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + type));
		return userRepository.findAllByRolesAndDeletedFalse(Set.of(role)).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsersWithIds(Collection<UUID> ids) {

		return userRepository.findByIdIn(ids)
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserById(UUID id) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return mapToResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserByEmail(String email) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return mapToResponse(user);
	}

	@Override
	public UserResponse register(RegistrationRequest request) {
		if (!request.password().equals(request.confirmPassword())) {
			throw new BusinessException(IdentityExceptions.REGISTRATION_ERROR, "Two Passwords didn't match!");
		}

		Role role = roleRepository.findByName(RoleType.USER)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		User user = User.builder()
				.email(request.email())
				.firstName(request.firstName())
				.lastName(request.lastName())
				.roles(Set.of(role))
				.password(passwordEncoder.encode(Optional.ofNullable(request.password()).orElse("password")))
				.build();
		return mapToResponse(userRepository.save(user));
	}

	@Override
	public UserResponse updateUser(UUID id, UpdateUserRequest request) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		user.setFirstName(request.firstName());
		user.setLastName(request.lastName());
		user.setPhone(request.phone());
		Address address = mapper.convertValue(request.address(), Address.class);
		address.setId(user.getAddress().getId());
		address.setDeleted(false);
		user.setAddress(address);
		user.setDob(request.dob());
		user.setGender(request.gender());
		user.setEnabled(request.enabled());

		return mapToResponse(userRepository.save(user));
	}

	@Override
	public UserResponse changePassword(PasswordChangeRequestDto request) {

		AuthenticatedUser currentUser = authenticationContext.getCurrentUser().orElse(null);

		User user = userRepository.findById(UUID.fromString(currentUser.getUserId()))
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (!passwordEncoder.matches(request.oldPassword(), user.getPassword()))
			throw new ForbiddenException("Incorrect old password.");

		user.setPassword(passwordEncoder.encode(request.newPassword()));

		return mapToResponse(userRepository.save(user));
	}

	@Override
	@Transactional
	public void deleteUser(UUID id) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		user.setDeleted(true);

		if (user.getAddress() != null) {
			user.getAddress().setDeleted(true);
		}

		userRepository.save(user);
	}

	private UserResponse mapToResponse(User user) {

		return UserResponse.builder()
				.id(user.getId())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.phone(user.getPhone())
				.gender(user.getGender())
				.address(user.getAddress())
				.dob(user.getDob())
				.enabled(user.isEnabled())
				.roles(
						user.getRoles().stream()
								.map(role -> role.getName())
								.collect(Collectors.toSet()))
				.createdAt(user.getCreatedAt())
				.updatedAt(user.getUpdatedAt())
				.build();
	}

	private UserResponse mapToResponse(User user, boolean ignoreTimeStamps) {

		return UserResponse.builder()
				.id(user.getId())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.phone(user.getPhone())
				.gender(user.getGender())
				.address(user.getAddress())
				.dob(user.getDob())
				.enabled(user.isEnabled())
				.roles(
						user.getRoles().stream()
								.map(role -> role.getName())
								.collect(Collectors.toSet()))
				.build();
	}
}