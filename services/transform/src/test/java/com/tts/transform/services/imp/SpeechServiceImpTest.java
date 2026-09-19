package com.tts.transform.services.imp;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.tts.transform.dto.SynthesizeRequest;
import com.tts.transform.exceptions.BusinessException;
import com.tts.transform.models.SpeechHistory;
import com.tts.transform.models.UsageMetrics;
import com.tts.transform.properties.DefaultProperties;
import com.tts.transform.services.AzureBlobStorageService;
import com.tts.transform.services.CurrentUserService;
import com.tts.transform.services.SpeechHistoryService;
import com.tts.transform.services.TtsProvider;
import com.tts.transform.services.UsageMetricsService;

@ExtendWith(MockitoExtension.class)
class SpeechServiceImpTest {

	@Mock
	private TtsProvider ttsProvider;

	@Mock
	private SpeechHistoryService historyService;

	@Mock
	private UsageMetricsService metricsService;

	@Mock
	private AzureBlobStorageService storageService;

	@Mock
	private CurrentUserService currentUser;

	@Mock
	private DefaultProperties properties;

	@Mock
	private DefaultProperties.LimitEnforcements limits;

	@InjectMocks
	private SpeechServiceImp speechService;

	private UUID userId;
	private SynthesizeRequest request;
	private UsageMetrics utilization;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();

		request = SynthesizeRequest.builder()
				.text("Hello, welcome to the application.")
				.language("en-US")
				.voice("en-US-JennyNeural")
				.style("general")
				.rate("0")
				.pitch("0")
				.volume("0")
				.build();

		utilization = UsageMetrics.builder()
				.ownerId(userId)
				.month(YearMonth.now())
				.utilized(100L)
				.maxLimit(1000L)
				.build();
	}

	@Test
	void synthesize_shouldGenerateAudioAndPersistHistoryAndUsage() {
		byte[] expectedAudio = "audio".getBytes();
		String audioPath = "speech/123.mp3";

		when(properties.getLimits()).thenReturn(limits);
		when(limits.getMaxOverdraftLimit()).thenReturn(25.0);
		when(currentUser.userId()).thenReturn(userId);

		when(metricsService.getUtilizationByMonth(YearMonth.now()))
				.thenReturn(utilization);
		when(ttsProvider.synthesize(
				request.text(),
				request.language(),
				request.voice(),
				request.style(),
				request.rate(),
				request.pitch(),
				request.volume()))
				.thenReturn(expectedAudio);
		when(storageService.upload(expectedAudio, "speech.mp3"))
				.thenReturn(audioPath);

		byte[] result = speechService.synthesize(request);

		assertArrayEquals(expectedAudio, result);

		verify(ttsProvider).synthesize(
				request.text(),
				request.language(),
				request.voice(),
				request.style(),
				request.rate(),
				request.pitch(),
				request.volume());

		verify(storageService).upload(expectedAudio, "speech.mp3");

		ArgumentCaptor<SpeechHistory> historyCaptor = ArgumentCaptor.forClass(SpeechHistory.class);

		verify(historyService).createRecord(historyCaptor.capture());

		SpeechHistory history = historyCaptor.getValue();

		assertEquals(request.text(), history.getText());
		assertEquals(request.language(), history.getLanguage());
		assertEquals(request.voice(), history.getVoice());
		assertEquals(audioPath, history.getAudioPath());
		assertEquals(userId, history.getOwnerId());

		verify(metricsService).updateUsage(utilization);
		assertEquals(
				100L + request.text().length(),
				utilization.getUtilized());
	}

	@Test
	void synthesize_shouldRejectWhenMonthlyLimitIsExhausted() {
		utilization.setUtilized(1000L);

		when(metricsService.getUtilizationByMonth(YearMonth.now()))
				.thenReturn(utilization);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> speechService.synthesize(request));

		assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatus());

		verify(ttsProvider, never()).synthesize(
				any(),
				any(),
				any(),
				any(),
				any(),
				any(),
				any());

		verify(storageService, never()).upload(any(), any());
		verify(historyService, never()).createRecord(any());
		verify(metricsService, never()).updateUsage(any());
	}

	@Test
	void synthesize_shouldRejectWhenMonthlyLimitHasBeenExceeded() {
		utilization.setUtilized(1100L);

		when(metricsService.getUtilizationByMonth(YearMonth.now()))
				.thenReturn(utilization);

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> speechService.synthesize(request));

		assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatus());

		verify(ttsProvider, never()).synthesize(
				any(),
				any(),
				any(),
				any(),
				any(),
				any(),
				any());
	}

	@Test
	void synthesize_shouldRejectWhenTextExceedsRemainingLimitIncludingOverdraft() {
		utilization.setUtilized(900L);
		utilization.setMaxLimit(1000L);

		when(properties.getLimits()).thenReturn(limits);
		when(limits.getMaxOverdraftLimit()).thenReturn(25.0);

		when(metricsService.getUtilizationByMonth(YearMonth.now()))
				.thenReturn(utilization);

		SynthesizeRequest longRequest = SynthesizeRequest.builder()
				.text("a".repeat(126))
				.language("en-US")
				.voice("en-US-JennyNeural")
				.build();

		BusinessException exception = assertThrows(
				BusinessException.class,
				() -> speechService.synthesize(longRequest));

		assertEquals(HttpStatus.CONTENT_TOO_LARGE, exception.getStatus());

		verify(ttsProvider, never()).synthesize(
				any(),
				any(),
				any(),
				any(),
				any(),
				any(),
				any());

		verify(storageService, never()).upload(any(), any());
		verify(historyService, never()).createRecord(any());
		verify(metricsService, never()).updateUsage(any());
	}

	@Test
	void synthesize_shouldAllowTextWithinRemainingLimitIncludingOverdraft() {
		utilization.setUtilized(900L);
		utilization.setMaxLimit(1000L);

		when(properties.getLimits()).thenReturn(limits);
		when(limits.getMaxOverdraftLimit()).thenReturn(25.0);
		when(metricsService.getUtilizationByMonth(YearMonth.now()))
				.thenReturn(utilization);

		byte[] expectedAudio = "audio".getBytes();

		when(ttsProvider.synthesize(
				any(),
				any(),
				any(),
				any(),
				any(),
				any(),
				any()))
				.thenReturn(expectedAudio);

		when(storageService.upload(any(), eq("speech.mp3")))
				.thenReturn("speech/123.mp3");

		SynthesizeRequest allowedRequest = SynthesizeRequest.builder()
				.text("a".repeat(120))
				.language("en-US")
				.voice("en-US-JennyNeural")
				.build();

		byte[] result = speechService.synthesize(allowedRequest);

		assertArrayEquals(expectedAudio, result);

		verify(ttsProvider).synthesize(
				allowedRequest.text(),
				allowedRequest.language(),
				allowedRequest.voice(),
				allowedRequest.style(),
				allowedRequest.rate(),
				allowedRequest.pitch(),
				allowedRequest.volume());

		verify(metricsService).updateUsage(utilization);
		assertEquals(1020L, utilization.getUtilized());
	}

	@Test
	void download_shouldRetrieveHistoryAndDownloadAudio() {
		UUID speechId = UUID.randomUUID();
		String audioPath = "speech/123.mp3";
		byte[] expectedAudio = "audio".getBytes();

		SpeechHistory history = SpeechHistory.builder()
				.id(speechId)
				.ownerId(userId)
				.audioPath(audioPath)
				.build();

		when(historyService.getHistoryById(speechId))
				.thenReturn(history);

		when(storageService.download(audioPath))
				.thenReturn(expectedAudio);

		byte[] result = speechService.download(speechId);

		assertArrayEquals(expectedAudio, result);

		verify(historyService).getHistoryById(speechId);
		verify(storageService).download(audioPath);
	}
}
