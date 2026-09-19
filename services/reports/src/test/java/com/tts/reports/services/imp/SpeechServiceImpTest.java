package com.tts.reports.services.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.tts.reports.dto.RestResponseDto;
import com.tts.reports.exceptions.BusinessException;
import com.tts.reports.exceptions.InternalCommunicationException;
import com.tts.reports.models.SpeechHistory;
import com.tts.reports.models.UsageMetrics;

@ExtendWith(MockitoExtension.class)
class SpeechServiceImpTest {

	@Mock
	private RestClient.Builder builder;

	@Mock
	private RestClient restClient;

	@Mock
	private RequestHeadersUriSpec<?> getSpec;

	@Mock
	private RequestHeadersSpec<?> getHeadersSpec;

	@Mock
	private ResponseSpec responseSpec;

	private SpeechServiceImp speechService;

	private final String speechServiceUrl = "http://transform-service";

	@BeforeEach
	void setUp() throws Exception {

		when(builder.build()).thenReturn(restClient);

		speechService = new SpeechServiceImp(builder);

		var field = SpeechServiceImp.class
				.getDeclaredField("SPEECH_SERVICE_URL");

		field.setAccessible(true);
		field.set(speechService, speechServiceUrl);
	}

	@Test
	void getAllUsageMetrics_shouldReturnUsageMetrics() {

		UsageMetrics metrics = UsageMetrics.builder()
				.id(UUID.randomUUID())
				.utilized(500L)
				.maxLimit(10000L)
				.build();

		@SuppressWarnings("unchecked")
		RestResponseDto<List<UsageMetrics>> response = mock(RestResponseDto.class);

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getHeadersSpec)
				.when(getSpec)
				.uri(speechServiceUrl + "/reports/metrics");

		when(getHeadersSpec.retrieve())
				.thenReturn(responseSpec);

		doReturn(response)
				.when(responseSpec)
				.body(ArgumentMatchers.<ParameterizedTypeReference<RestResponseDto<List<UsageMetrics>>>>any());

		when(response.getData())
				.thenReturn(List.of(metrics));

		List<UsageMetrics> result = speechService.getAllUsageMetrics();

		assertEquals(1, result.size());
		assertEquals(metrics, result.get(0));
	}

	@Test
	void getAllUsageMetrics_shouldThrowInternalCommunicationExceptionOnHttpError() {

		HttpStatusCodeException exception = new HttpStatusCodeException(
				HttpStatus.BAD_REQUEST,
				"Bad Request") {
		};

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getHeadersSpec)
				.when(getSpec)
				.uri(speechServiceUrl + "/reports/metrics");

		when(getHeadersSpec.retrieve())
				.thenReturn(responseSpec);

		when(responseSpec.body(
				ArgumentMatchers.<ParameterizedTypeReference<RestResponseDto<List<UsageMetrics>>>>any()))
				.thenThrow(exception);

		assertThrows(
				InternalCommunicationException.class,
				() -> speechService.getAllUsageMetrics());
	}

	@Test
	void getAllUsageMetrics_shouldThrowBusinessExceptionOnUnexpectedError() {

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getHeadersSpec)
				.when(getSpec)
				.uri(speechServiceUrl + "/reports/metrics");

		when(getHeadersSpec.retrieve())
				.thenThrow(new RuntimeException("Connection failed"));

		assertThrows(
				BusinessException.class,
				() -> speechService.getAllUsageMetrics());
	}

	@Test
	void getAllHistoryRecords_shouldReturnHistoryRecords() {

		SpeechHistory history = SpeechHistory.builder()
				.id(UUID.randomUUID())
				.text("Hello")
				.language("en-US")
				.voice("en-US-JennyNeural")
				.createdAt(LocalDateTime.now())
				.build();

		@SuppressWarnings("unchecked")
		RestResponseDto<List<SpeechHistory>> response = mock(RestResponseDto.class);

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getHeadersSpec)
				.when(getSpec)
				.uri(speechServiceUrl + "/reports/history");

		when(getHeadersSpec.retrieve())
				.thenReturn(responseSpec);

		doReturn(response)
				.when(responseSpec)
				.body(ArgumentMatchers.<ParameterizedTypeReference<RestResponseDto<List<SpeechHistory>>>>any());

		when(response.getData())
				.thenReturn(List.of(history));

		List<SpeechHistory> result = speechService.getAllHistoryRecords();

		assertEquals(1, result.size());
		assertEquals(history, result.get(0));
	}

	@Test
	void getAllHistoryRecords_shouldThrowInternalCommunicationExceptionOnHttpError() {

		HttpStatusCodeException exception = new HttpStatusCodeException(
				HttpStatus.NOT_FOUND,
				"Not Found") {
		};

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getHeadersSpec)
				.when(getSpec)
				.uri(speechServiceUrl + "/reports/history");

		when(getHeadersSpec.retrieve())
				.thenReturn(responseSpec);

		when(responseSpec.body(
				ArgumentMatchers.<ParameterizedTypeReference<RestResponseDto<List<SpeechHistory>>>>any()))
				.thenThrow(exception);

		assertThrows(
				InternalCommunicationException.class,
				() -> speechService.getAllHistoryRecords());
	}

	@Test
	void getAllHistoryRecords_shouldThrowBusinessExceptionOnUnexpectedError() {

		doReturn(getSpec)
				.when(restClient)
				.get();

		doReturn(getHeadersSpec)
				.when(getSpec)
				.uri(speechServiceUrl + "/reports/history");

		when(getHeadersSpec.retrieve())
				.thenThrow(new RuntimeException("Connection failed"));

		assertThrows(
				BusinessException.class,
				() -> speechService.getAllHistoryRecords());
	}
}