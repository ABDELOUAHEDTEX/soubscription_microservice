package com.transport.urbain.repository;

import com.transport.urbain.model.Role;
import com.transport.urbain.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity persistence operations.
 * 
 * <p>Provides data access methods for managing roles, including:
 * <ul>
 *   <li>Finding roles by name</li>
 *   <li>Checking role existence</li>
 * </ul>
 * 
 * <p>Roles are identified by their enum name (ADMIN, PASSENGER, DRIVER)
 * and are typically predefined in the system. This repository primarily
 * supports role-based access control operations.
 * 
 * <p>Extends JpaRepository to provide standard CRUD operations.
 * 
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see com.transport.urbain.model.Role
 * @see com.transport.urbain.model.RoleName
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by name.
     * 
     * @param name the role name to search for
     * @return an Optional containing the role if found
     */
    Optional<Role> findByName(RoleName name);

    /**
     * Checks if a role with the given name exists.
     * 
     * @param name the role name to check
     * @return true if role exists, false otherwise
     */
    Boolean existsByName(RoleName name);

}
