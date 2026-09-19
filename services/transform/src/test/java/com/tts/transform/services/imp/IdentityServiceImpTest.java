package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.tts.transform.dto.RestResponseDto;
import com.tts.transform.dto.UserResponseDto;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.exceptions.InternalCommunicationException;

class IdentityServiceImpTest {

	private RestClient restClient;

	private IdentityServiceImp service;

	@BeforeEach
	void setUp() {

		restClient = mock(RestClient.class);

		RestClient.Builder builder = mock(RestClient.Builder.class);

		when(builder.build()).thenReturn(restClient);

		service = new IdentityServiceImp(builder);

		ReflectionTestUtils.setField(
				service,
				"IDENTITY_SERVICE_URL",
				"http://identity");
	}

	@Test
	void getAllUsers_shouldReturnUsers() {

		UserResponseDto user = mock(UserResponseDto.class);

		RestResponseDto<List<UserResponseDto>> response = RestResponseDto.<List<UserResponseDto>>builder()
				.data(List.of(user))
				.build();

		@SuppressWarnings("rawtypes")
		RequestHeadersUriSpec getSpec = mock(RequestHeadersUriSpec.class);

		ResponseSpec responseSpec = mock(ResponseSpec.class);

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getSpec)
				.when(getSpec)
				.uri("http://identity/users/");

		doReturn(responseSpec)
				.when(getSpec)
				.retrieve();

		doReturn(response)
				.when(responseSpec)
				.body(ArgumentMatchers
						.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any());

		List<UserResponseDto> result = service.getAllUsers();

		assertEquals(List.of(user), result);
	}

	@Test
	void getAllUsers_shouldThrowInternalCommunicationExceptionWhenIdentityServiceReturnsHttpError() {

		@SuppressWarnings("rawtypes")
		RequestHeadersUriSpec getSpec = mock(RestClient.RequestHeadersUriSpec.class);

		ResponseSpec responseSpec = mock(ResponseSpec.class);

		HttpStatusCodeException exception = new HttpClientErrorException(
				HttpStatus.NOT_FOUND,
				"Not Found",
				"identity error".getBytes(),
				null);

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getSpec)
				.when(getSpec)
				.uri("http://identity/users/");

		doReturn(responseSpec)
				.when(getSpec)
				.retrieve();

		when(responseSpec.body(
				ArgumentMatchers
						.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any()))
				.thenThrow(exception);

		InternalCommunicationException thrown = assertThrows(
				InternalCommunicationException.class,
				() -> service.getAllUsers());

		assertEquals("identity error", thrown.getMessage());
		assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
	}

	@Test
	void getAllUsers_shouldThrowBusinessExceptionWhenUnexpectedErrorOccurs() {

		@SuppressWarnings("rawtypes")
		RequestHeadersUriSpec getSpec = mock(RestClient.RequestHeadersUriSpec.class);

		ResponseSpec responseSpec = mock(ResponseSpec.class);

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getSpec)
				.when(getSpec)
				.uri("http://identity/users/");

		doReturn(responseSpec)
				.when(getSpec)
				.retrieve();

		when(responseSpec.body(
				ArgumentMatchers
						.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any()))
				.thenThrow(new RuntimeException("connection failed"));

		BusinessException thrown = assertThrows(
				BusinessException.class,
				() -> service.getAllUsers());

		assertEquals(
				"Error Connecting with Identity Service",
				thrown.getMessage());
	}

	@Test
	void getAllUsersByIds_shouldReturnUsersMappedById() {

		UUID id1 = UUID.randomUUID();
		UUID id2 = UUID.randomUUID();

		UserResponseDto user1 = mock(UserResponseDto.class);
		UserResponseDto user2 = mock(UserResponseDto.class);

		when(user1.id()).thenReturn(id1);
		when(user2.id()).thenReturn(id2);

		RestClient.RequestBodyUriSpec postSpec = mock(RestClient.RequestBodyUriSpec.class);

		RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);

		ResponseSpec responseSpec = mock(ResponseSpec.class);

		RestResponseDto<List<UserResponseDto>> response = RestResponseDto.<List<UserResponseDto>>builder()
				.data(List.of(user1, user2))
				.build();

		doReturn(postSpec)
				.when(restClient)
				.post();

		doReturn(postSpec)
				.when(postSpec)
				.uri("http://identity/users/search");

		doReturn(bodySpec)
				.when(postSpec)
				.body(ArgumentMatchers.any(Object.class));

		doReturn(responseSpec)
				.when(bodySpec)
				.retrieve();

		doReturn(response)
				.when(responseSpec)
				.body(ArgumentMatchers
						.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any());

		Map<UUID, UserResponseDto> result = service.getAllUsersByIds(Set.of(id1, id2));

		assertEquals(2, result.size());
		assertEquals(user1, result.get(id1));
		assertEquals(user2, result.get(id2));
	}

	@Test
	void getAllUsersByIds_shouldReturnEmptyMapForEmptyResult() {

		RestClient.RequestBodyUriSpec postSpec = mock(RestClient.RequestBodyUriSpec.class);

		RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);

		ResponseSpec responseSpec = mock(ResponseSpec.class);

		RestResponseDto<List<UserResponseDto>> response = RestResponseDto.<List<UserResponseDto>>builder()
				.data(List.of())
				.build();

		doReturn(postSpec)
				.when(restClient)
				.post();

		doReturn(postSpec)
				.when(postSpec)
				.uri("http://identity/users/search");

		doReturn(bodySpec)
				.when(postSpec)
				.body(ArgumentMatchers.any(Object.class));

		doReturn(responseSpec)
				.when(bodySpec)
				.retrieve();

		doReturn(response)
				.when(responseSpec)
				.body(ArgumentMatchers
						.<ParameterizedTypeReference<RestResponseDto<List<UserResponseDto>>>>any());

		Map<UUID, UserResponseDto> result = service.getAllUsersByIds(Set.of());

		assertEquals(Map.of(), result);
	}
}