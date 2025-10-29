package com.transport.urbain.controller;

import com.transport.urbain.dto.request.UpdateProfileRequest;
import com.transport.urbain.dto.response.ProfileResponse;
import com.transport.urbain.model.Gender;
import com.transport.urbain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProfileController.
 * 
 * <p>Tests user profile management endpoints including:
 * <ul>
 *   <li>Profile retrieval</li>
 *   <li>Profile updates with full data</li>
 *   <li>Partial profile updates</li>
 * </ul>
 * 
 * <p>Verifies HTTP responses, service interactions, and proper handling
 * of profile data including personal information, preferences, and notification settings.
 */
@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private ProfileController profileController;

    @Test
    void shouldGetUserProfile() {
        // Given
        Long userId = 1L;
        ProfileResponse profileResponse = ProfileResponse.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .build();

        when(userService.getUserProfile(userId)).thenReturn(profileResponse);

        // When
        ResponseEntity<ProfileResponse> response = profileController.getUserProfile(userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);

        verify(userService).getUserProfile(userId);
    }

    @Test
    void shouldUpdateUserProfile() {
        // Given
        Long userId = 1L;
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .gender(Gender.FEMALE)
                .address("123 Main St")
                .city("New York")
                .country("USA")
                .build();

        ProfileResponse profileResponse = ProfileResponse.builder()
                .id(1L)
                .gender(Gender.FEMALE)
                .build();

        when(userService.updateUserProfile(userId, request)).thenReturn(profileResponse);

        // When
        ResponseEntity<ProfileResponse> response = profileController.updateUserProfile(userId, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGender()).isEqualTo(Gender.FEMALE);

        verify(userService).updateUserProfile(userId, request);
    }

    @Test
    void shouldUpdateUserProfileWithPartialData() {
        // Given
        Long userId = 1L;
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .bio("Updated bio")
                .build();

        ProfileResponse profileResponse = ProfileResponse.builder()
                .id(1L)
                .bio("Updated bio")
                .build();

        when(userService.updateUserProfile(userId, request)).thenReturn(profileResponse);

        // When
        ResponseEntity<ProfileResponse> response = profileController.updateUserProfile(userId, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBio()).isEqualTo("Updated bio");

        verify(userService).updateUserProfile(userId, request);
    }
}

