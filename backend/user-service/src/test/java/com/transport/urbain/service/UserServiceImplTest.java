package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.ProfileMapper;
import com.transport.urbain.dto.mapper.UserMapper;
import com.transport.urbain.dto.request.ChangePasswordRequest;
import com.transport.urbain.dto.request.UpdateProfileRequest;
import com.transport.urbain.dto.response.ProfileResponse;
import com.transport.urbain.dto.response.UserResponse;
import com.transport.urbain.event.producer.UserEventProducer;
import com.transport.urbain.exception.InvalidCredentialsException;
import com.transport.urbain.exception.UserNotFoundException;
import com.transport.urbain.model.*;
import com.transport.urbain.repository.UserProfileRepository;
import com.transport.urbain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserServiceImpl.
 * 
 * <p>Tests user management service implementation including:
 * <ul>
 *   <li>User CRUD operations</li>
 *   <li>Profile management</li>
 *   <li>Password change with validation</li>
 *   <li>Email and phone verification</li>
 *   <li>Account unlocking</li>
 * </ul>
 * 
 * <p>Verifies:
 * <ul>
 *   <li>Data retrieval and transformation</li>
 *   <li>Partial update handling</li>
 *   <li>Password validation</li>
 *   <li>Event publishing</li>
 *   <li>Exception handling</li>
 *   <li>Data integrity</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserProfile testProfile;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .id(1L)
                .name(RoleName.PASSENGER)
                .description("Default passenger role")
                .build();

        testProfile = UserProfile.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .address("123 Main St")
                .city("New York")
                .country("USA")
                .postalCode("10001")
                .nationality("American")
                .notificationsEnabled(true)
                .emailNotificationsEnabled(true)
                .smsNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .build();

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .status(UserStatus.ACTIVE)
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .emailVerified(true)
                .phoneVerified(false)
                .authProvider(AuthProvider.LOCAL)
                .roles(new HashSet<>(Set.of(testRole)))
                .profile(testProfile)
                .build();

        testProfile.setUser(testUser);
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // Given
        Long userId = 1L;
        UserResponse expectedResponse = UserResponse.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponse(testUser)).thenReturn(expectedResponse);

        // When
        UserResponse response = userService.getUserById(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(userId);
        verify(userMapper).toUserResponse(testUser);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with id");

        verify(userRepository).findById(userId);
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void shouldGetUserByEmailSuccessfully() {
        // Given
        String email = "test@example.com";
        UserResponse expectedResponse = UserResponse.builder()
                .id(1L)
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponse(testUser)).thenReturn(expectedResponse);

        // When
        UserResponse response = userService.getUserByEmail(email);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(email);

        verify(userRepository).findByEmail(email);
        verify(userMapper).toUserResponse(testUser);
    }

    @Test
    void shouldGetAllUsersSuccessfully() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser));
        Page<UserResponse> expectedPage = new PageImpl<>(List.of(new UserResponse()));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toUserResponse(testUser)).thenReturn(new UserResponse());

        // When
        Page<UserResponse> response = userService.getAllUsers(pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isNotEmpty();

        verify(userRepository).findAll(pageable);
    }

    @Test
    void shouldSearchUsersSuccessfully() {
        // Given
        String keyword = "John";
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser));

        when(userRepository.searchUsers(keyword, pageable)).thenReturn(userPage);
        when(userMapper.toUserResponse(testUser)).thenReturn(new UserResponse());

        // When
        Page<UserResponse> response = userService.searchUsers(keyword, pageable);

        // Then
        assertThat(response).isNotNull();

        verify(userRepository).searchUsers(keyword, pageable);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // Given
        Long userId = 1L;
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("+9876543210")
                .build();

        UserResponse expectedResponse = UserResponse.builder()
                .id(userId)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserResponse(testUser)).thenReturn(expectedResponse);

        // When
        UserResponse response = userService.updateUser(userId, request);

        // Then
        assertThat(response).isNotNull();
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(testUser);
        verify(userEventProducer).publishUserUpdated(any());
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        // Given
        Long userId = 1L;
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("OldPassword@123")
                .newPassword("NewPassword@123")
                .confirmPassword("NewPassword@123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getCurrentPassword(), testUser.getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.changePassword(userId, request);

        // Then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(request.getCurrentPassword(), "encodedPassword");
        verify(passwordEncoder).encode(request.getNewPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
        // Given
        Long userId = 1L;
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("WrongPassword")
                .newPassword("NewPassword@123")
                .confirmPassword("NewPassword@123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getCurrentPassword(), testUser.getPassword()))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Current password is incorrect");

        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(request.getCurrentPassword(), testUser.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenPasswordsDoNotMatch() {
        // Given
        Long userId = 1L;
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("OldPassword@123")
                .newPassword("NewPassword@123")
                .confirmPassword("DifferentPassword@123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getCurrentPassword(), testUser.getPassword()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("New password and confirm password do not match");

        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(request.getCurrentPassword(), testUser.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(userEventProducer).publishUserDeleted(any());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(userCaptor.getValue().getEnabled()).isFalse();
    }

    @Test
    void shouldGetUserProfileSuccessfully() {
        // Given
        Long userId = 1L;
        ProfileResponse expectedResponse = ProfileResponse.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .build();

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(testProfile));
        when(profileMapper.toProfileResponse(testProfile)).thenReturn(expectedResponse);

        // When
        ProfileResponse response = userService.getUserProfile(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(userProfileRepository).findByUserId(userId);
        verify(profileMapper).toProfileResponse(testProfile);
    }

    @Test
    void shouldUpdateUserProfileSuccessfully() {
        // Given
        Long userId = 1L;
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .gender(Gender.FEMALE)
                .address("456 Oak Ave")
                .city("Los Angeles")
                .country("USA")
                .notificationsEnabled(false)
                .build();

        ProfileResponse expectedResponse = ProfileResponse.builder()
                .id(1L)
                .gender(Gender.FEMALE)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testProfile);
        when(profileMapper.toProfileResponse(testProfile)).thenReturn(expectedResponse);

        // When
        ProfileResponse response = userService.updateUserProfile(userId, request);

        // Then
        assertThat(response).isNotNull();

        verify(userRepository).findById(userId);
        verify(userProfileRepository).save(any(UserProfile.class));
        verify(profileMapper).toProfileResponse(testProfile);
    }

    @Test
    void shouldCreateProfileWhenUpdatingUserWithoutProfile() {
        // Given
        Long userId = 1L;
        testUser.setProfile(null);
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .gender(Gender.MALE)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testProfile);
        when(profileMapper.toProfileResponse(testProfile)).thenReturn(new ProfileResponse());

        // When
        ProfileResponse response = userService.updateUserProfile(userId, request);

        // Then
        assertThat(response).isNotNull();

        verify(userRepository).findById(userId);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void shouldVerifyEmailSuccessfully() {
        // Given
        Long userId = 1L;
        String token = "verificationToken";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.verifyEmail(userId, token);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmailVerified()).isTrue();
    }

    @Test
    void shouldVerifyPhoneSuccessfully() {
        // Given
        Long userId = 1L;
        String code = "123456";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.verifyPhone(userId, code);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPhoneVerified()).isTrue();
    }

    @Test
    void shouldUnlockAccountSuccessfully() {
        // Given
        Long userId = 1L;
        testUser.setAccountNonLocked(false);
        testUser.setFailedLoginAttempts(5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.unlockAccount(userId);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getAccountNonLocked()).isTrue();
        assertThat(userCaptor.getValue().getFailedLoginAttempts()).isEqualTo(0);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForUnlock() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.unlockAccount(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with id");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldOnlyUpdateProvidedFields() {
        // Given
        Long userId = 1L;
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .firstName("Jane")
                // lastName and phoneNumber are not set
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserResponse(testUser)).thenReturn(new UserResponse());

        // When
        userService.updateUser(userId, request);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getFirstName()).isEqualTo("Jane");
        assertThat(savedUser.getLastName()).isEqualTo("Doe"); // Unchanged
    }
}

