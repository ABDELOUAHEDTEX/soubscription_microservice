package com.transport.urbain.dto.mapper;

import com.transport.urbain.dto.response.UserResponse;
import com.transport.urbain.model.Role;
import com.transport.urbain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper interface for converting between User entity and UserResponse DTO.
 * 
 * <p>This mapper uses MapStruct to generate implementation at compile time.
 * It handles the mapping from {@link com.transport.urbain.model.User} to
 * {@link com.transport.urbain.dto.response.UserResponse}.
 * 
 * <p>The mapper converts the Set of Role entities to a Set of role name strings
 * for simpler client consumption.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a User entity to a UserResponse DTO.
     * 
     * <p>Maps the user roles from Role entities to their string names.
     * 
     * @param user the user entity to convert
     * @return the user response DTO containing user information
     */
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRoleNames")
    UserResponse toUserResponse(User user);

    /**
     * Helper method to convert a Set of Role entities to a Set of role name strings.
     * 
     * <p>Extracts the name from each role and returns it as a string.
     * 
     * @param roles the set of roles to convert
     * @return a set of role name strings
     */
    @Named("rolesToRoleNames")
    default Set<String> rolesToRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}
