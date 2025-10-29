package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user in the system.
 * 
 * <p>This entity stores user account information including authentication details,
 * personal information, security settings, and role assignments. It supports both
 * local authentication and OAuth providers.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Email-based authentication with password encryption</li>
 *   <li>Role-based access control (many-to-many with Role entity)</li>
 *   <li>Account locking after failed login attempts</li>
 *   <li>Email and phone verification flags</li>
 *   <li>OAuth provider integration (Google, Facebook, Apple)</li>
 *   <li>Audit fields for creation and modification timestamps</li>
 *   <li>One-to-one relationship with UserProfile for extended details</li>
 * </ul>
 * 
 * <p>Security features:
 * <ul>
 *   <li>Failed login attempt tracking</li>
 *   <li>Account locking/unlocking</li>
 *   <li>Email and phone verification status</li>
 *   <li>Account enabled/disabled state</li>
 * </ul>
 * 
 * @see UserProfile
 * @see Role
 * @see UserStatus
 * @see AuthProvider
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = {"profile", "roles"})
@EqualsAndHashCode(exclude = {"profile", "roles"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(unique = true, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean phoneVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    private LocalDateTime lastLoginAt;

    private LocalDateTime lockedAt;

    @Column(length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AuthProvider authProvider;

    private String providerId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Helper methods
    
    /**
     * Adds a role to the user.
     * 
     * @param role the role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Removes a role from the user.
     * 
     * @param role the role to remove
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Increments the failed login attempts counter.
     */
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }

    /**
     * Resets the failed login attempts counter to zero.
     */
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
    }

    /**
     * Locks the user account due to too many failed login attempts.
     * Sets the lockedAt timestamp and disables account access.
     */
    public void lock() {
        this.accountNonLocked = false;
        this.lockedAt = LocalDateTime.now();
    }

    /**
     * Unlocks the user account.
     * Resets the account access and clears the failed attempts counter.
     */
    public void unlock() {
        this.accountNonLocked = true;
        this.lockedAt = null;
        this.failedLoginAttempts = 0;
    }
}
