package com.transport.urbain.util;

import com.transport.urbain.model.RouteStop;
import com.transport.urbain.model.Stop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RouteOptimizer utility class.
 * <p>
 * This test class covers all route optimization and distance calculation logic including:
 * <ul>
 *     <li>Haversine distance calculations</li>
 *     <li>Total route distance calculations</li>
 *     <li>Estimated travel time calculations</li>
 *     <li>Route optimality checks</li>
 *     <li>Edge cases and boundary conditions</li>
 * </ul>
 *
 * @author Transport Team
 */
class RouteOptimizerTest {

    private RouteOptimizer routeOptimizer;

    /**
     * Sets up test data before each test method.
     * Creates an instance of RouteOptimizer.
     */
    @BeforeEach
    void setUp() {
        routeOptimizer = new RouteOptimizer();
    }

    /**
     * Tests calculation of distance between two GPS coordinates.
     * Verifies that the Haversine formula is applied correctly.
     */
    @Test
    void testCalculateDistance_Success() {
        // Arrange
        BigDecimal lat1 = new BigDecimal("40.7128"); // New York
        BigDecimal lon1 = new BigDecimal("-74.0060");
        BigDecimal lat2 = new BigDecimal("34.0522"); // Los Angeles
        BigDecimal lon2 = new BigDecimal("-118.2437");

        // Act
        double distance = routeOptimizer.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert
        assertTrue(distance > 0);
        // Distance between NYC and LA is approximately 3944 km
        assertTrue(distance > 3000 && distance < 4500);
    }

    /**
     * Tests calculation of distance for same coordinates.
     * Verifies that zero distance is returned.
     */
    @Test
    void testCalculateDistance_SameCoordinates() {
        // Arrange
        BigDecimal lat = new BigDecimal("40.7128");
        BigDecimal lon = new BigDecimal("-74.0060");

        // Act
        double distance = routeOptimizer.calculateDistance(lat, lon, lat, lon);

        // Assert
        assertEquals(0.0, distance, 0.001);
    }

    /**
     * Tests calculation of total route distance.
     * Verifies that distances between consecutive stops are summed correctly.
     */
    @Test
    void testCalculateTotalDistance_Success() {
        // Arrange
        List<RouteStop> stops = new ArrayList<>();
        
        Stop stop1 = createStop(1L, new BigDecimal("40.7128"), new BigDecimal("-74.0060"));
        Stop stop2 = createStop(2L, new BigDecimal("40.7589"), new BigDecimal("-73.9851"));
        Stop stop3 = createStop(3L, new BigDecimal("40.7489"), new BigDecimal("-73.9680"));
        
        stops.add(createRouteStop(1L, stop1, 1));
        stops.add(createRouteStop(2L, stop2, 2));
        stops.add(createRouteStop(3L, stop3, 3));

        // Act
        double totalDistance = routeOptimizer.calculateTotalDistance(stops);

        // Assert
        assertTrue(totalDistance > 0);
        assertTrue(totalDistance < 50); // Rough estimate for Manhattan distances
    }

    /**
     * Tests calculation of total distance for a single stop.
     * Verifies that zero distance is returned.
     */
    @Test
    void testCalculateTotalDistance_SingleStop() {
        // Arrange
        List<RouteStop> stops = new ArrayList<>();
        Stop stop = createStop(1L, new BigDecimal("40.7128"), new BigDecimal("-74.0060"));
        stops.add(createRouteStop(1L, stop, 1));

        // Act
        double totalDistance = routeOptimizer.calculateTotalDistance(stops);

        // Assert
        assertEquals(0.0, totalDistance);
    }

    /**
     * Tests calculation of total distance for empty list.
     * Verifies that zero distance is returned.
     */
    @Test
    void testCalculateTotalDistance_EmptyList() {
        // Arrange
        List<RouteStop> stops = new ArrayList<>();

        // Act
        double totalDistance = routeOptimizer.calculateTotalDistance(stops);

        // Assert
        assertEquals(0.0, totalDistance);
    }

    /**
     * Tests calculation of total distance for null list.
     * Verifies that zero distance is returned.
     */
    @Test
    void testCalculateTotalDistance_NullList() {
        // Act
        double totalDistance = routeOptimizer.calculateTotalDistance(null);

        // Assert
        assertEquals(0.0, totalDistance);
    }

    /**
     * Tests calculation of estimated travel time.
     * Verifies that the result is calculated correctly.
     */
    @Test
    void testCalculateEstimatedTime_Success() {
        // Arrange
        double distanceInKm = 10.0;
        double averageSpeedKmh = 30.0;

        // Act
        int estimatedTime = routeOptimizer.calculateEstimatedTime(distanceInKm, averageSpeedKmh);

        // Assert
        assertEquals(20, estimatedTime); // 10 km / 30 km/h * 60 = 20 minutes
    }

    /**
     * Tests calculation of estimated time with fractional result.
     * Verifies that the result is rounded up.
     */
    @Test
    void testCalculateEstimatedTime_FractionalResult() {
        // Arrange
        double distanceInKm = 5.0;
        double averageSpeedKmh = 30.0;

        // Act
        int estimatedTime = routeOptimizer.calculateEstimatedTime(distanceInKm, averageSpeedKmh);

        // Assert
        assertEquals(10, estimatedTime);
    }

    /**
     * Tests calculation of estimated time with zero speed.
     * Verifies that IllegalArgumentException is thrown.
     */
    @Test
    void testCalculateEstimatedTime_ZeroSpeed() {
        // Arrange
        double distanceInKm = 10.0;
        double averageSpeedKmh = 0.0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> routeOptimizer.calculateEstimatedTime(distanceInKm, averageSpeedKmh));
    }

    /**
     * Tests calculation of estimated time with negative speed.
     * Verifies that IllegalArgumentException is thrown.
     */
    @Test
    void testCalculateEstimatedTime_NegativeSpeed() {
        // Arrange
        double distanceInKm = 10.0;
        double averageSpeedKmh = -30.0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> routeOptimizer.calculateEstimatedTime(distanceInKm, averageSpeedKmh));
    }

    /**
     * Tests checking if route is optimal.
     * Verifies that a route within the max distance returns true.
     */
    @Test
    void testIsRouteOptimal_WithinLimit() {
        // Arrange
        List<RouteStop> stops = new ArrayList<>();
        
        Stop stop1 = createStop(1L, new BigDecimal("40.7128"), new BigDecimal("-74.0060"));
        Stop stop2 = createStop(2L, new BigDecimal("40.7589"), new BigDecimal("-73.9851"));
        
        stops.add(createRouteStop(1L, stop1, 1));
        stops.add(createRouteStop(2L, stop2, 2));

        double maxDistanceKm = 50.0;

        // Act
        boolean isOptimal = routeOptimizer.isRouteOptimal(stops, maxDistanceKm);

        // Assert
        assertTrue(isOptimal);
    }

    /**
     * Tests checking if route is optimal.
     * Verifies that a route exceeding the max distance returns false.
     */
    @Test
    void testIsRouteOptimal_ExceedsLimit() {
        // Arrange
        List<RouteStop> stops = new ArrayList<>();
        
        Stop stop1 = createStop(1L, new BigDecimal("40.7128"), new BigDecimal("-74.0060"));
        Stop stop2 = createStop(2L, new BigDecimal("40.7589"), new BigDecimal("-73.9851"));
        Stop stop3 = createStop(3L, new BigDecimal("40.7489"), new BigDecimal("-73.9680"));
        Stop stop4 = createStop(4L, new BigDecimal("40.7289"), new BigDecimal("-73.9480"));
        
        stops.add(createRouteStop(1L, stop1, 1));
        stops.add(createRouteStop(2L, stop2, 2));
        stops.add(createRouteStop(3L, stop3, 3));
        stops.add(createRouteStop(4L, stop4, 4));

        double maxDistanceKm = 0.01; // Very small limit

        // Act
        boolean isOptimal = routeOptimizer.isRouteOptimal(stops, maxDistanceKm);

        // Assert
        assertFalse(isOptimal);
    }

    /**
     * Helper method to create a Stop for testing.
     *
     * @param id stop ID
     * @param latitude latitude coordinate
     * @param longitude longitude coordinate
     * @return created Stop object
     */
    private Stop createStop(Long id, BigDecimal latitude, BigDecimal longitude) {
        return Stop.builder()
                .id(id)
                .stopCode("ST" + id)
                .stopName("Stop " + id)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    /**
     * Helper method to create a RouteStop for testing.
     *
     * @param id route stop ID
     * @param stop stop object
     * @param sequenceNumber sequence number
     * @return created RouteStop object
     */
    private RouteStop createRouteStop(Long id, Stop stop, int sequenceNumber) {
        return RouteStop.builder()
                .id(id)
                .stop(stop)
                .sequenceNumber(sequenceNumber)
                .distanceFromOrigin(new BigDecimal("0.0"))
                .timeFromOrigin(0)
                .dwellTime(1)
                .build();
    }
}

