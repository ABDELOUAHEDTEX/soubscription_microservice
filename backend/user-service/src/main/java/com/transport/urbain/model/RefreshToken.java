package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a refresh token for JWT authentication.
 * 
 * <p>This entity stores refresh tokens used to obtain new access tokens when
 * the current access token expires. Refresh tokens have longer expiration times
 * than access tokens and can be revoked.
 * 
 * <p>Token management features:
 * <ul>
 *   <li>Token expiration tracking</li>
 *   <li>Token revocation support</li>
 *   <li>Device and IP address tracking for security</li>
 *   <li>Many-to-one relationship with User</li>
 * </ul>
 * 
 * <p>Security considerations:
 * <ul>
 *   <li>Tokens can be revoked during logout</li>
 *   <li>Expired tokens are automatically invalid</li>
 *   <li>Device information is tracked for audit purposes</li>
 * </ul>
 * 
 * @see User
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 100)
    private String deviceInfo;

    @Column(length = 50)
    private String ipAddress;

    @Column(nullable = false)
    @Builder.Default
    private Boolean revoked = false;

    private LocalDateTime revokedAt;

    /**
     * Checks if the refresh token has expired.
     * 
     * @return true if the token is expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    /**
     * Revokes the refresh token, marking it as no longer valid.
     * Sets the revoked flag and revocation timestamp.
     */
    public void revoke() {
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
    }
}
