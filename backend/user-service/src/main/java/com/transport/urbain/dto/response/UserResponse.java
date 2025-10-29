package com.transport.urbain.dto.response;

import com.transport.urbain.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO representing user information.
 * 
 * <p>Contains user account details returned from user retrieval operations.
 * This response includes basic user information along with status flags,
 * roles, and verification status.
 * 
 * <p>User information includes:
 * <ul>
 *   <li>User identifier</li>
 *   <li>Contact information: email, phone number</li>
 *   <li>Personal details: first name, last name</li>
 *   <li>Account status and flags</li>
 *   <li>User roles</li>
 *   <li>Verification status (email and phone)</li>
 *   <li>Profile image URL</li>
 *   <li>Last login timestamp</li>
 *   <li>Creation and update timestamps</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserStatus status;
    private Set<String> roles;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean enabled;
    private String profileImageUrl;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
