package com.transport.urbain.controller;

import com.transport.urbain.dto.request.ChangePasswordRequest;
import com.transport.urbain.dto.request.UpdateProfileRequest;
import com.transport.urbain.dto.response.UserResponse;
import com.transport.urbain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing users.
 * 
 * <p>This controller handles HTTP requests related to user management operations,
 * including user CRUD operations, search, password management, and account locking.
 * All endpoints require JWT authentication and enforce role-based access control.
 * 
 * <p>User operations include:
 * <ul>
 *   <li>Retrieving users by ID or email</li>
 *   <li>Listing and searching all users (admin only)</li>
 *   <li>Updating user information</li>
 *   <li>Changing user passwords</li>
 *   <li>Deleting users (admin only)</li>
 *   <li>Unlocking locked accounts (admin only)</li>
 * </ul>
 * 
 * <p>Access Control:
 * <ul>
 *   <li>Users can view and update their own information</li>
 *   <li>Administrators have full access to all user operations</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves user information by user ID.
     * 
     * <p>Allows users to retrieve their own information or allows administrators
     * to retrieve any user's information.
     * 
     * @param id the ID of the user to retrieve
     * @return the user response containing user information
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         does not have permission to view this user
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieves user information by email address.
     * 
     * <p>This endpoint is restricted to administrators only and allows finding
     * users by their unique email address.
     * 
     * @param email the email address of the user to retrieve
     * @return the user response containing user information
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         is not an administrator
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Retrieves all users with pagination support.
     * 
     * <p>This endpoint returns a paginated list of all users in the system.
     * Only administrators can access this endpoint.
     * 
     * @param pageable the pagination parameters (page, size, sort)
     * @return a page of user responses
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         is not an administrator
     */
    @GetMapping
    @Operation(summary = "Get all users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    /**
     * Searches for users by keyword.
     * 
     * <p>This endpoint allows administrators to search for users using a keyword
     * that matches against user names, emails, or other searchable fields.
     * Results are paginated.
     * 
     * @param keyword the search keyword
     * @param pageable the pagination parameters (page, size, sort)
     * @return a page of matching user responses
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         is not an administrator
     */
    @GetMapping("/search")
    @Operation(summary = "Search users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(keyword, pageable));
    }

    /**
     * Updates user information.
     * 
     * <p>Allows users to update their own information or allows administrators
     * to update any user's information. The request must contain valid update data.
     * 
     * @param id the ID of the user to update
     * @param request the update request containing new user data
     * @return the updated user response
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         does not have permission to update this user
     * @throws jakarta.validation.ConstraintViolationException if validation fails
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Changes the password for a user account.
     * 
     * <p>Users can only change their own password. The request must include
     * the current password for verification and the new password.
     * 
     * @param id the ID of the user whose password to change
     * @param request the password change request containing old and new passwords
     * @return no content on success
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         does not have permission to change this password
     * @throws jakarta.validation.ConstraintViolationException if validation fails
     */
    @PutMapping("/{id}/change-password")
    @Operation(summary = "Change user password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a user account.
     * 
     * <p>This operation permanently removes a user from the system.
     * Only administrators can delete user accounts.
     * 
     * @param id the ID of the user to delete
     * @return no content on success
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         is not an administrator
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Unlocks a locked user account.
     * 
     * <p>Accounts may become locked due to multiple failed login attempts or
     * administrative action. This endpoint allows administrators to unlock
     * accounts so users can log in again.
     * 
     * @param id the ID of the user account to unlock
     * @return no content on success
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         is not an administrator
     */
    @PostMapping("/{id}/unlock")
    @Operation(summary = "Unlock user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unlockAccount(@PathVariable Long id) {
        userService.unlockAccount(id);
        return ResponseEntity.noContent().build();
    }
}
