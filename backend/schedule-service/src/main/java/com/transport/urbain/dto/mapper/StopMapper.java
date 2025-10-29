package com.transport.urbain.dto.mapper;

import com.transport.urbain.dto.response.StopResponse;
import com.transport.urbain.model.Stop;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between Stop entities and DTOs.
 * <p>
 * This interface uses MapStruct to automatically generate mapping implementations
 * for converting Stop domain models to StopResponse DTOs used in API responses.
 */
@Mapper(componentModel = "spring")
public interface StopMapper {

    /**
     * Maps a Stop entity to a StopResponse DTO.
     * <p>
     * Converts stop details including location coordinates, amenities, and
     * accessibility information.
     *
     * @param stop the stop entity to convert
     * @return stop response DTO containing stop information
     */
    StopResponse toStopResponse(Stop stop);
}
