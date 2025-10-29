package com.transport.urbain.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations.
 * 
 * <p>Provides static methods for formatting and parsing LocalDateTime objects
 * to and from string representations using a consistent format.
 * 
 * <p>Features:
 * <ul>
 *   <li>Date/time formatting: LocalDateTime to "yyyy-MM-dd HH:mm:ss" string</li>
 *   <li>Date/time parsing: "yyyy-MM-dd HH:mm:ss" string to LocalDateTime</li>
 *   <li>Null-safe operations</li>
 * </ul>
 * 
 * <p>Format pattern: {@value yyyy-MM-dd HH:mm:ss}
 */
public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Formats a LocalDateTime to a string using the standard format.
     * 
     * @param dateTime the LocalDateTime to format
     * @return formatted string in "yyyy-MM-dd HH:mm:ss" format, or null if input is null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FORMATTER);
    }

    /**
     * Parses a string to a LocalDateTime using the standard format.
     * 
     * @param dateTimeStr the string to parse
     * @return parsed LocalDateTime, or null if input is null or blank
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, FORMATTER);
    }
}
