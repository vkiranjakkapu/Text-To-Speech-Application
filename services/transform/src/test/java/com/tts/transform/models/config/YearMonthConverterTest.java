package com.tts.transform.models.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class YearMonthConverterTest {

	private YearMonthConverter converter;

	@BeforeEach
	void setUp() {
		converter = new YearMonthConverter();
	}

	@Test
	void convertToDatabaseColumn_shouldConvertYearMonthToString() {

		YearMonth value = YearMonth.of(2026, 9);

		assertEquals(
				"2026-09",
				converter.convertToDatabaseColumn(value));
	}

	@Test
	void convertToDatabaseColumn_shouldReturnNullForNullValue() {

		assertNull(
				converter.convertToDatabaseColumn(null));
	}

	@Test
	void convertToEntityAttribute_shouldConvertStringToYearMonth() {

		assertEquals(
				YearMonth.of(2026, 9),
				converter.convertToEntityAttribute("2026-09"));
	}

	@Test
	void convertToEntityAttribute_shouldReturnNullForNullValue() {

		assertNull(
				converter.convertToEntityAttribute(null));
	}
}