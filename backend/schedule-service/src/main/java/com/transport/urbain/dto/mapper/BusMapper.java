package com.transport.urbain.dto.mapper;

import com.transport.urbain.dto.response.BusResponse;
import com.transport.urbain.model.Bus;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between Bus entities and DTOs.
 * <p>
 * This interface uses MapStruct to automatically generate mapping implementations
 * for converting Bus domain models to BusResponse DTOs used in API responses.
 */
@Mapper(componentModel = "spring")
public interface BusMapper {

    /**
     * Maps a Bus entity to a BusResponse DTO.
     * <p>
     * Converts all bus details including specifications, amenities, and status.
     *
     * @param bus the bus entity to convert
     * @return bus response DTO containing bus information
     */
    BusResponse toBusResponse(Bus bus);
}
