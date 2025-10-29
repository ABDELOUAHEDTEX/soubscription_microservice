package com.transport.urbain.dto.mapper;

import com.transport.urbain.dto.response.ProfileResponse;
import com.transport.urbain.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between UserProfile entity and ProfileResponse DTO.
 * 
 * <p>This mapper uses MapStruct to generate implementation at compile time.
 * It handles the mapping from {@link com.transport.urbain.model.UserProfile} to
 * {@link com.transport.urbain.dto.response.ProfileResponse}.
 * 
 * <p>The mapper extracts the user ID from the nested user relationship and
 * maps all profile-specific fields to the response DTO.
 */
@Mapper(componentModel = "spring")
public interface ProfileMapper {

    /**
     * Converts a UserProfile entity to a ProfileResponse DTO.
     * 
     * @param userProfile the user profile entity to convert
     * @return the profile response DTO containing profile information
     */
    @Mapping(source = "user.id", target = "userId")
    ProfileResponse toProfileResponse(UserProfile userProfile);
}
