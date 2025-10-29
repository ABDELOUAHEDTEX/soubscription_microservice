package com.transport.urbain.model;

/**
 * Enum representing the operational status of a bus.
 * <p>
 * Tracks the current state of buses in the fleet to manage availability
 * and operational capabilities.
 */
public enum BusStatus {
    /**
     * Bus is active and available for assignment
     */
    ACTIVE,

    /**
     * Bus is currently in active service on a route
     */
    IN_SERVICE,

    /**
     * Bus is undergoing maintenance or repair
     */
    MAINTENANCE,

    /**
     * Bus is temporarily out of service for various reasons
     */
    OUT_OF_SERVICE,

    /**
     * Bus has been permanently retired from the fleet
     */
    RETIRED
}