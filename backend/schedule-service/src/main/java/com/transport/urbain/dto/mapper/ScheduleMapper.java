package com.transport.urbain.dto.mapper;

import com.transport.urbain.dto.response.ScheduleResponse;
import com.transport.urbain.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between Schedule entities and DTOs.
 * <p>
 * This interface uses MapStruct to automatically generate mapping implementations
 * for converting Schedule domain models to ScheduleResponse DTOs.
 * <p>
 * The mapper flattens nested objects (route and bus) into the response DTO
 * for easier consumption by API clients.
 */
@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    /**
     * Maps a Schedule entity to a ScheduleResponse DTO.
     * <p>
     * Extracts schedule information along with related route and bus details,
     * flattening nested objects into the response.
     *
     * @param schedule the schedule entity to convert
     * @return schedule response DTO containing schedule, route, and bus information
     */
    @Mapping(source = "route.id", target = "routeId")
    @Mapping(source = "route.routeNumber", target = "routeNumber")
    @Mapping(source = "route.routeName", target = "routeName")
    @Mapping(source = "bus.id", target = "busId")
    @Mapping(source = "bus.busNumber", target = "busNumber")
    ScheduleResponse toScheduleResponse(Schedule schedule);
}
