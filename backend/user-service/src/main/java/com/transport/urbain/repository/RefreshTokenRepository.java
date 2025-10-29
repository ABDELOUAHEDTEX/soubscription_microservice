package com.transport.urbain.repository;

import com.transport.urbain.model.RefreshToken;
import com.transport.urbain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RefreshToken entity persistence operations.
 * 
 * <p>Provides data access methods for managing refresh tokens, including:
 * <ul>
 *   <li>Finding tokens by token string or user</li>
 *   <li>Deleting tokens for a specific user</li>
 *   <li>Removing expired tokens</li>
 *   <li>Revoking all tokens for a user</li>
 * </ul>
 * 
 * <p>Includes bulk operations for token management and cleanup:
 * <ul>
 *   <li>Token expiration cleanup</li>
 *   <li>User logout token revocation</li>
 *   <li>Account deletion token removal</li>
 * </ul>
 * 
 * <p>Extends JpaRepository to provide standard CRUD operations.
 * 
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see com.transport.urbain.model.RefreshToken
 * @see com.transport.urbain.model.User
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token by its token string.
     * 
     * @param token the token string to search for
     * @return an Optional containing the token if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Finds all refresh tokens for a specific user.
     * 
     * @param user the user whose tokens to retrieve
     * @return a list of refresh tokens for the user
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Deletes all refresh tokens for a specific user.
     * Used during account deletion or security events.
     * 
     * @param user the user whose tokens should be deleted
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);

    /**
     * Deletes all expired refresh tokens.
     * Used for periodic cleanup to remove stale tokens from the database.
     * 
     * @param now the current timestamp for comparison
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Revokes all refresh tokens for a specific user by marking them as revoked.
     * Does not delete the tokens but marks them as invalid for future use.
     * Used during logout from all devices or security incidents.
     * 
     * @param user the user whose tokens should be revoked
     * @param now the timestamp when the revocation occurred
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now WHERE rt.user = :user")
    void revokeAllUserTokens(@Param("user") User user, @Param("now") LocalDateTime now);
}
