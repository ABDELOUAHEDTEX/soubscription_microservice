package com.transport.urbain.service;

import com.transport.urbain.dto.request.ChangePasswordRequest;
import com.transport.urbain.dto.request.UpdateProfileRequest;
import com.transport.urbain.dto.response.ProfileResponse;
import com.transport.urbain.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for user management operations.
 * 
 * <p>Provides methods for managing users and their profiles, including:
 * <ul>
 *   <li>User CRUD operations (read, update, delete)</li>
 *   <li>User profile management</li>
 *   <li>Password management</li>
 *   <li>Account verification</li>
 *   <li>Account unlocking</li>
 * </ul>
 * 
 * <p>All operations are scoped to either individual users or administrators.
 * Profile operations manage extended user information beyond basic account data.
 */
public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    Page<UserResponse> getAllUsers(Pageable pageable);

    Page<UserResponse> searchUsers(String keyword, Pageable pageable);

    UserResponse updateUser(Long id, UpdateProfileRequest request);

    void changePassword(Long id, ChangePasswordRequest request);

    void deleteUser(Long id);

    ProfileResponse getUserProfile(Long userId);

    ProfileResponse updateUserProfile(Long userId, UpdateProfileRequest request);

    void verifyEmail(Long userId, String token);

    void verifyPhone(Long userId, String code);

    void unlockAccount(Long userId);
}
