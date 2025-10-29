package com.transport.urbain.controller;

import com.transport.urbain.dto.request.UpdateProfileRequest;
import com.transport.urbain.dto.response.ProfileResponse;
import com.transport.urbain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user profiles.
 * 
 * <p>This controller handles HTTP requests related to user profile operations,
 * including retrieving and updating user profile information. All endpoints
 * require JWT authentication and enforce role-based access control.
 * 
 * <p>Profile operations include:
 * <ul>
 *   <li>Retrieving user profile by user ID</li>
 *   <li>Updating user profile information</li>
 * </ul>
 * 
 * <p>Access Control:
 * <ul>
 *   <li>Users can view their own profile or admins can view any profile</li>
 *   <li>Only the profile owner can update their own profile</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/users/{userId}/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Profile", description = "User profile management endpoints")
public class ProfileController {

    private final UserService userService;

    /**
     * Retrieves the profile information for a specific user.
     * 
     * <p>This endpoint allows users to retrieve their own profile or allows
     * administrators to retrieve any user's profile.
     * 
     * @param userId the ID of the user whose profile to retrieve
     * @return the profile response containing user information
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         does not have permission to view this profile
     */
    @GetMapping
    @Operation(summary = "Get user profile")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    /**
     * Updates the profile information for a specific user.
     * 
     * <p>This endpoint allows users to update their own profile information.
     * Only the profile owner can update their profile.
     * 
     * @param userId the ID of the user whose profile to update
     * @param request the update profile request containing new profile data
     * @return the updated profile response
     * @throws org.springframework.security.access.AccessDeniedException if the user
     *         does not have permission to update this profile
     * @throws jakarta.validation.ConstraintViolationException if validation fails
     */
    @PutMapping
    @Operation(summary = "Update user profile")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<ProfileResponse> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, request));
    }
}
