package com.transport.urbain.repository;

import com.transport.urbain.model.User;
import com.transport.urbain.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity persistence operations.
 * 
 * <p>Provides data access methods for managing users, including:
 * <ul>
 *   <li>Finding users by email, phone number, or OAuth provider</li>
 *   <li>Checking user existence</li>
 *   <li>Filtering users by status</li>
 *   <li>Searching users by keywords</li>
 *   <li>Eager loading roles for authentication</li>
 * </ul>
 * 
 * <p>Extends JpaRepository to provide standard CRUD operations and pagination support.
 * 
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see com.transport.urbain.model.User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by phone number.
     * 
     * @param phoneNumber the phone number to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Checks if a user with the given email exists.
     * 
     * @param email the email address to check
     * @return true if user exists, false otherwise
     */
    Boolean existsByEmail(String email);

    /**
     * Checks if a user with the given phone number exists.
     * 
     * @param phoneNumber the phone number to check
     * @return true if user exists, false otherwise
     */
    Boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Finds an enabled user by email.
     * Only returns users that are enabled (not disabled).
     * 
     * @param email the email address to search for
     * @return an Optional containing the enabled user if found
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.enabled = true")
    Optional<User> findByEmailAndEnabled(@Param("email") String email);

    /**
     * Finds a user by email and eagerly loads their roles.
     * This is useful for authentication and authorization purposes.
     * 
     * @param email the email address to search for
     * @return an Optional containing the user with roles loaded
     */
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    /**
     * Finds users by status with pagination support.
     * 
     * @param status the user status to filter by
     * @param pageable pagination parameters
     * @return a page of users with the specified status
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    /**
     * Finds a user by OAuth provider and provider ID.
     * Used to locate users who logged in via external OAuth providers.
     * 
     * @param provider the authentication provider (e.g., GOOGLE, FACEBOOK)
     * @param providerId the user's ID from the OAuth provider
     * @return an Optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE u.authProvider = :provider AND u.providerId = :providerId")
    Optional<User> findByAuthProviderAndProviderId(
            @Param("provider") String provider,
            @Param("providerId") String providerId
    );

    /**
     * Searches for users by keyword in first name, last name, or email.
     * Performs case-insensitive partial matching.
     * 
     * @param keyword the search keyword
     * @param pageable pagination parameters
     * @return a page of matching users
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
