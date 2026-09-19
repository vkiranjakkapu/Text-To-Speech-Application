package com.tts.reports.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.tts.reports.dto.FetchUsersRequestDto;
import com.tts.reports.dto.RestResponseDto;
import com.tts.reports.dto.UserResponseDto;
import com.tts.reports.exceptions.BusinessException;
import com.tts.reports.exceptions.InternalCommunicationException;

@ExtendWith(MockitoExtension.class)
class IdentityServiceImpTest {

	@Mock
	private RestClient.Builder builder;

	@Mock
	private RestClient restClient;

	@Mock
	private RequestHeadersUriSpec<?> getSpec;

	@Mock
	private RequestHeadersSpec<?> getHeadersSpec;

	@Mock
	private RequestBodyUriSpec postSpec;

	@Mock
	private RequestBodySpec postBodySpec;

	@Mock
	private ResponseSpec responseSpec;

	private IdentityServiceImp identityService;

	private final String identityUrl = "http://identity-service";

	@BeforeEach
	void setUp() throws Exception {

		when(builder.build()).thenReturn(restClient);

		identityService = new IdentityServiceImp(builder);

		var field = IdentityServiceImp.class
				.getDeclaredField("IDENTITY_SERVICE_URL");

		field.setAccessible(true);
		field.set(identityService, identityUrl);
	}

	@Test
	void getAllUsers_shouldReturnUsers() {

		UserResponseDto user = UserResponseDto.builder()
				.id(UUID.randomUUID())
				.firstName("John")
				.lastName("Doe")
				.createdAt(LocalDateTime.now())
				.build();

		@SuppressWarnings("unchecked")
		RestResponseDto<List<UserResponseDto>> response = mock(RestResponseDto.class);

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getHeadersSpec)
				.when(getSpec)
				.uri(identityUrl + "/users/");

		when(getHeadersSpec.retrieve()).thenReturn(responseSpec);

		doReturn(response).when(responseSpec).body(
				ArgumentMatchers.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any());

		when(response.getData()).thenReturn(List.of(user));

		List<UserResponseDto> result = identityService.getAllUsers();

		assertEquals(1, result.size());
		assertEquals(user, result.get(0));

		verify(restClient).get();
		verify(getSpec).uri(identityUrl + "/users/");
		verify(getHeadersSpec).retrieve();
	}

	@Test
	void getAllUsers_shouldThrowInternalCommunicationExceptionOnHttpError() {

		HttpStatusCodeException exception = new HttpStatusCodeException(HttpStatus.BAD_REQUEST, "Bad Request") {
		};

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getHeadersSpec)
				.when(getSpec)
				.uri(identityUrl + "/users/");

		when(getHeadersSpec.retrieve())
				.thenReturn(responseSpec);

		when(responseSpec.body(
				ArgumentMatchers.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any()))
				.thenThrow(exception);

		assertThrows(
				InternalCommunicationException.class,
				() -> identityService.getAllUsers());
	}

	@Test
	void getAllUsers_shouldThrowBusinessExceptionOnUnexpectedError() {

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getHeadersSpec)
				.when(getSpec)
				.uri(identityUrl + "/users/");
		when(getHeadersSpec.retrieve())
				.thenThrow(new RuntimeException("Connection failed"));

		assertThrows(
				BusinessException.class,
				() -> identityService.getAllUsers());
	}

	@Test
	void getAllUsersByIds_shouldReturnUsersMappedById() {

		UUID id1 = UUID.randomUUID();
		UUID id2 = UUID.randomUUID();

		UserResponseDto user1 = UserResponseDto.builder()
				.id(id1)
				.firstName("John")
				.build();

		UserResponseDto user2 = UserResponseDto.builder()
				.id(id2)
				.firstName("Jane")
				.build();

		@SuppressWarnings("unchecked")
		RestResponseDto<List<UserResponseDto>> response = mock(RestResponseDto.class);

		when(restClient.post()).thenReturn(postSpec);
		when(postSpec.uri(identityUrl + "/users/search"))
				.thenReturn(postBodySpec);
		doReturn(postBodySpec)
				.when(postBodySpec)
				.body(ArgumentMatchers.any(FetchUsersRequestDto.class));
		when(postBodySpec.retrieve()).thenReturn(responseSpec);

		doReturn(response).when(responseSpec).body(
				ArgumentMatchers.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any());

		when(response.getData()).thenReturn(List.of(user1, user2));

		Map<UUID, UserResponseDto> result = identityService.getAllUsersByIds(Set.of(id1, id2));

		assertEquals(2, result.size());
		assertEquals(user1, result.get(id1));
		assertEquals(user2, result.get(id2));

		verify(restClient).post();
		verify(postSpec).uri(identityUrl + "/users/search");
	}

	@Test
	void getAllUsersByIds_shouldReturnEmptyMapWhenNoUsersFound() {

		@SuppressWarnings("unchecked")
		RestResponseDto<List<UserResponseDto>> response = mock(RestResponseDto.class);

		when(restClient.post()).thenReturn(postSpec);
		when(postSpec.uri(identityUrl + "/users/search"))
				.thenReturn(postBodySpec);
		doReturn(postBodySpec)
				.when(postBodySpec)
				.body(ArgumentMatchers.any(FetchUsersRequestDto.class));
		when(postBodySpec.retrieve()).thenReturn(responseSpec);

		doReturn(response).when(responseSpec).body(
				ArgumentMatchers.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any());

		when(response.getData()).thenReturn(List.of());

		Map<UUID, UserResponseDto> result = identityService.getAllUsersByIds(Set.of(UUID.randomUUID()));

		assertEquals(0, result.size());
	}

	@Test
	void getAllUsersByIds_shouldThrowInternalCommunicationExceptionOnHttpError() {

		HttpStatusCodeException exception = new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Not Found") {
		};

		doReturn(postSpec)
				.when(restClient)
				.post();

		doReturn(postBodySpec)
				.when(postSpec)
				.uri(identityUrl + "/users/search");

		doReturn(postBodySpec)
				.when(postBodySpec)
				.body(ArgumentMatchers.any(FetchUsersRequestDto.class));

		when(postBodySpec.retrieve())
				.thenReturn(responseSpec);

		when(responseSpec.body(
				ArgumentMatchers.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any()))
				.thenThrow(exception);

		assertThrows(
				InternalCommunicationException.class,
				() -> identityService.getAllUsersByIds(Set.of(UUID.randomUUID())));
	}

	@Test
	void getAllUsersByIds_shouldThrowBusinessExceptionOnUnexpectedError() {

		when(restClient.post()).thenReturn(postSpec);
		when(postSpec.uri(identityUrl + "/users/search"))
				.thenReturn(postBodySpec);
		doReturn(postBodySpec)
				.when(postBodySpec)
				.body(ArgumentMatchers.any(FetchUsersRequestDto.class));
		when(postBodySpec.retrieve())
				.thenThrow(new RuntimeException("Connection failed"));

		assertThrows(
				BusinessException.class,
				() -> identityService.getAllUsersByIds(Set.of(UUID.randomUUID())));
	}
}