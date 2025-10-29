package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing a permission in the system.
 * 
 * <p>This entity defines granular permissions that determine what actions
 * can be performed on specific resources. Permissions are assigned to roles,
 * which are then assigned to users.
 * 
 * <p>Permission structure:
 * <ul>
 *   <li>Name: unique identifier for the permission</li>
 *   <li>Resource: the target resource (e.g., "user", "ticket")</li>
 *   <li>Action: the allowed action (e.g., "read", "write", "delete")</li>
 *   <li>Description: human-readable explanation of the permission</li>
 * </ul>
 * 
 * <p>Example permissions:
 * <ul>
 *   <li>user:read - allows reading user data</li>
 *   <li>user:write - allows creating/updating user data</li>
 *   <li>ticket:delete - allows deleting tickets</li>
 * </ul>
 * 
 * <p>Relationship with roles is many-to-many, allowing flexible permission management.
 * 
 * @see Role
 */
@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 100)
    private String resource;

    @Column(nullable = false, length = 50)
    private String action;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}