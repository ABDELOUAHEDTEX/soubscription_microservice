package com.transport.urbain.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the DateUtil utility class.
 * 
 * <p>Tests date/time formatting and parsing operations including:
 * <ul>
 *   <li>LocalDateTime to string formatting</li>
 *   <li>String to LocalDateTime parsing</li>
 *   <li>Null and blank string handling</li>
 *   <li>Round-trip formatting and parsing</li>
 * </ul>
 * 
 * <p>Verifies:
 * <ul>
 *   <li>Correct date format "yyyy-MM-dd HH:mm:ss"</li>
 *   <li>Null safety</li>
 *   <li>Data preservation through format/parse cycle</li>
 * </ul>
 */
class DateUtilTest {

    @Test
    void shouldFormatDateTime() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);

        // When
        String formatted = DateUtil.formatDateTime(dateTime);

        // Then
        assertThat(formatted).isEqualTo("2024-01-15 14:30:45");
    }

    @Test
    void shouldReturnNullForNullDateTime() {
        // When
        String formatted = DateUtil.formatDateTime(null);

        // Then
        assertThat(formatted).isNull();
    }

    @Test
    void shouldParseDateTime() {
        // Given
        String dateTimeStr = "2024-01-15 14:30:45";

        // When
        LocalDateTime parsed = DateUtil.parseDateTime(dateTimeStr);

        // Then
        assertThat(parsed).isEqualTo(LocalDateTime.of(2024, 1, 15, 14, 30, 45));
    }

    @Test
    void shouldReturnNullForNullString() {
        // When
        LocalDateTime parsed = DateUtil.parseDateTime(null);

        // Then
        assertThat(parsed).isNull();
    }

    @Test
    void shouldReturnNullForBlankString() {
        // When
        LocalDateTime parsed = DateUtil.parseDateTime("   ");

        // Then
        assertThat(parsed).isNull();
    }

    @Test
    void shouldReturnNullForEmptyString() {
        // When
        LocalDateTime parsed = DateUtil.parseDateTime("");

        // Then
        assertThat(parsed).isNull();
    }

    @Test
    void shouldFormatAndParseRoundTrip() {
        // Given
        LocalDateTime original = LocalDateTime.of(2024, 12, 31, 23, 59, 59);

        // When
        String formatted = DateUtil.formatDateTime(original);
        LocalDateTime parsed = DateUtil.parseDateTime(formatted);

        // Then
        assertThat(parsed).isEqualTo(original);
    }
}

