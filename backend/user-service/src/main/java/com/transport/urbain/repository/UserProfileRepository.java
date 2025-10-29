package com.transport.urbain.repository;

import com.transport.urbain.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserProfile entity persistence operations.
 * 
 * <p>Provides data access methods for managing user profiles, including:
 * <ul>
 *   <li>Finding profiles by user ID</li>
 *   <li>Deleting profiles by user ID</li>
 * </ul>
 * 
 * <p>Since UserProfile has a one-to-one relationship with User, most operations
 * are scoped to a specific user ID.
 * 
 * <p>Extends JpaRepository to provide standard CRUD operations.
 * 
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see com.transport.urbain.model.UserProfile
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Finds a user profile by user ID.
     * 
     * @param userId the ID of the user
     * @return an Optional containing the profile if found
     */
    Optional<UserProfile> findByUserId(Long userId);

    /**
     * Deletes a user profile by user ID.
     * Used when cleaning up user data during account deletion.
     * 
     * @param userId the ID of the user whose profile should be deleted
     */
    void deleteByUserId(Long userId);
}
