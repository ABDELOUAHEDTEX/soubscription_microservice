package com.transport.urbain.util;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Utility class for time calculations and manipulations.
 * <p>
 * Provides static methods for computing time differences, adding/subtracting time,
 * checking time ranges, and calculating stop durations based on distance and speed.
 */
public class TimeCalculator {

    /**
     * Calculates the duration between two times in minutes.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return duration in minutes (always positive)
     */
    public static int calculateDurationInMinutes(LocalTime startTime, LocalTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        return (int) duration.toMinutes();
    }

    /**
     * Adds minutes to a LocalTime.
     *
     * @param time the base time
     * @param minutes minutes to add
     * @return the resulting time
     */
    public static LocalTime addMinutes(LocalTime time, int minutes) {
        return time.plusMinutes(minutes);
    }

    /**
     * Subtracts minutes from a LocalTime.
     *
     * @param time the base time
     * @param minutes minutes to subtract
     * @return the resulting time
     */
    public static LocalTime subtractMinutes(LocalTime time, int minutes) {
        return time.minusMinutes(minutes);
    }

    /**
     * Checks if a time falls within a given time range (inclusive).
     *
     * @param time the time to check
     * @param start start of the range
     * @param end end of the range
     * @return true if time is between start and end (inclusive), false otherwise
     */
    public static boolean isTimeBetween(LocalTime time, LocalTime start, LocalTime end) {
        return !time.isBefore(start) && !time.isAfter(end);
    }

    /**
     * Calculates stop duration based on distance and average speed.
     *
     * @param distanceInKm distance in kilometers
     * @param averageSpeed average speed in kilometers per hour
     * @return duration in minutes
     * @throws IllegalArgumentException if average speed is not positive
     */
    public static int calculateStopTime(int distanceInKm, int averageSpeed) {
        if (averageSpeed <= 0) {
            throw new IllegalArgumentException("Average speed must be greater than 0");
        }
        return (int) Math.ceil((double) distanceInKm / averageSpeed * 60);
    }
}
