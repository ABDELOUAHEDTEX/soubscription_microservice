package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user role in the system.
 * 
 * <p>This entity defines roles that can be assigned to users to control access
 * to resources. Each role has a set of permissions that determine what actions
 * users with that role can perform.
 * 
 * <p>Roles are used for:
 * <ul>
 *   <li>Grouping users by their responsibilities</li>
 *   <li>Defining access permissions</li>
 *   <li>Simplifying authorization management</li>
 * </ul>
 * 
 * <p>Relationship with users is many-to-many, allowing users to have multiple
 * roles and roles to be assigned to multiple users.
 * 
 * <p>Relationship with permissions is many-to-many, allowing roles to have
 * multiple permissions and permissions to belong to multiple roles.
 * 
 * @see RoleName
 * @see Permission
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RoleName name;

    @Column(length = 500)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Helper methods
    
    /**
     * Adds a permission to the role.
     * 
     * @param permission the permission to add
     */
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    /**
     * Removes a permission from the role.
     * 
     * @param permission the permission to remove
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }
}
