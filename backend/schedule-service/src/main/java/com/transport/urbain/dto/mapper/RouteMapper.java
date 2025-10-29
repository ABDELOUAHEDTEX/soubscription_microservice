package com.transport.urbain.dto.mapper;

import com.transport.urbain.dto.response.RouteResponse;
import com.transport.urbain.model.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between Route entities and DTOs.
 * <p>
 * This interface uses MapStruct to automatically generate mapping implementations
 * for converting Route domain models to RouteResponse DTOs used in API responses.
 */
@Mapper(componentModel = "spring")
public interface RouteMapper {

    /**
     * Maps a Route entity to a RouteResponse DTO.
     * <p>
     * Converts route information and automatically calculates the number of stops
     * on the route based on the associated route stops.
     *
     * @param route the route entity to convert
     * @return route response DTO containing route information and stop count
     */
    @Mapping(target = "numberOfStops", expression = "java(route.getRouteStops() != null ? route.getRouteStops().size() : 0)")
    RouteResponse toRouteResponse(Route route);
}
