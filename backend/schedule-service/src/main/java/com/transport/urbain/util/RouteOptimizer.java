package com.transport.urbain.util;

import com.transport.urbain.model.RouteStop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Utility class for route optimization and distance calculations.
 * <p>
 * Provides methods for calculating distances between geographic coordinates
 * using the Haversine formula, computing route distances, estimating travel times,
 * and evaluating route optimization criteria.
 */
@Component
@Slf4j
public class RouteOptimizer {

    /**
     * Earth's radius in kilometers for distance calculations
     */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the total distance of a route by summing distances between consecutive stops.
     * <p>
     * Uses the Haversine formula to calculate great-circle distances between GPS coordinates
     * of adjacent stops in the route sequence.
     *
     * @param stops list of route stops in sequence order
     * @return total distance in kilometers
     */
    public double calculateTotalDistance(List<RouteStop> stops) {
        if (stops == null || stops.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;
        for (int i = 0; i < stops.size() - 1; i++) {
            RouteStop current = stops.get(i);
            RouteStop next = stops.get(i + 1);
            totalDistance += calculateDistance(
                    current.getStop().getLatitude(),
                    current.getStop().getLongitude(),
                    next.getStop().getLatitude(),
                    next.getStop().getLongitude()
            );
        }

        return totalDistance;
    }

    /**
     * Calculates the distance between two GPS coordinates using the Haversine formula.
     * <p>
     * The Haversine formula determines the great-circle distance between two points
     * on a sphere given their latitudes and longitudes. Returns the distance in kilometers.
     *
     * @param lat1 latitude of the first point
     * @param lon1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lon2 longitude of the second point
     * @return distance in kilometers
     */
    public double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1.doubleValue())) *
                        Math.cos(Math.toRadians(lat2.doubleValue())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates estimated travel time based on distance and average speed.
     *
     * @param distanceInKm distance in kilometers
     * @param averageSpeedKmh average speed in kilometers per hour
     * @return estimated time in minutes
     * @throws IllegalArgumentException if average speed is not positive
     */
    public int calculateEstimatedTime(double distanceInKm, double averageSpeedKmh) {
        if (averageSpeedKmh <= 0) {
            throw new IllegalArgumentException("Average speed must be greater than 0");
        }
        return (int) Math.ceil((distanceInKm / averageSpeedKmh) * 60);
    }

    /**
     * Checks if a route meets optimal distance criteria.
     * <p>
     * Determines whether the total distance of a route is within acceptable limits.
     *
     * @param stops list of route stops
     * @param maxDistanceKm maximum acceptable distance in kilometers
     * @return true if route distance is within limits, false otherwise
     */
    public boolean isRouteOptimal(List<RouteStop> stops, double maxDistanceKm) {
        double totalDistance = calculateTotalDistance(stops);
        return totalDistance <= maxDistanceKm;
    }
}
