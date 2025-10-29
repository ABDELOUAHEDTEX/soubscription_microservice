package com.transport.urbain.controller;

import com.transport.urbain.dto.request.ChangePasswordRequest;
import com.transport.urbain.dto.request.UpdateProfileRequest;
import com.transport.urbain.dto.response.UserResponse;
import com.transport.urbain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserController.
 * 
 * <p>Tests user management endpoints including:
 * <ul>
 *   <li>User retrieval by ID and email</li>
 *   <li>User search and pagination</li>
 *   <li>User updates and deletion</li>
 *   <li>Password management</li>
 *   <li>Account unlocking</li>
 * </ul>
 * 
 * <p>Verifies HTTP responses, service interactions, and proper handling
 * of user management operations.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldGetUserById() {
        // Given
        Long userId = 1L;
        UserResponse userResponse = UserResponse.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userService.getUserById(userId)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> response = userController.getUserById(userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(userId);

        verify(userService).getUserById(userId);
    }

    @Test
    void shouldGetUserByEmail() {
        // Given
        String email = "test@example.com";
        UserResponse userResponse = UserResponse.builder()
                .email(email)
                .build();

        when(userService.getUserByEmail(email)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> response = userController.getUserByEmail(email);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo(email);

        verify(userService).getUserByEmail(email);
    }

    @Test
    void shouldGetAllUsers() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<UserResponse> userPage = Page.empty();

        when(userService.getAllUsers(pageable)).thenReturn(userPage);

        // When
        ResponseEntity<Page<UserResponse>> response = userController.getAllUsers(pageable);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        verify(userService).getAllUsers(pageable);
    }

    @Test
    void shouldSearchUsers() {
        // Given
        String keyword = "test";
        PageRequest pageable = PageRequest.of(0, 10);
        Page<UserResponse> userPage = Page.empty();

        when(userService.searchUsers(keyword, pageable)).thenReturn(userPage);

        // When
        ResponseEntity<Page<UserResponse>> response = userController.searchUsers(keyword, pageable);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        verify(userService).searchUsers(keyword, pageable);
    }

    @Test
    void shouldUpdateUser() {
        // Given
        Long userId = 1L;
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(userId)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(userService.updateUser(userId, request)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> response = userController.updateUser(userId, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFirstName()).isEqualTo("Jane");

        verify(userService).updateUser(userId, request);
    }

    @Test
    void shouldChangePassword() {
        // Given
        Long userId = 1L;
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("OldPassword@123")
                .newPassword("NewPassword@123")
                .confirmPassword("NewPassword@123")
                .build();

        doNothing().when(userService).changePassword(userId, request);

        // When
        ResponseEntity<Void> response = userController.changePassword(userId, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(userService).changePassword(userId, request);
    }

    @Test
    void shouldDeleteUser() {
        // Given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // When
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(userService).deleteUser(userId);
    }

    @Test
    void shouldUnlockAccount() {
        // Given
        Long userId = 1L;
        doNothing().when(userService).unlockAccount(userId);

        // When
        ResponseEntity<Void> response = userController.unlockAccount(userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(userService).unlockAccount(userId);
    }
}

