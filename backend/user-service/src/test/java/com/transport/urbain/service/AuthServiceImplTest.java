package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.UserMapper;
import com.transport.urbain.dto.request.LoginRequest;
import com.transport.urbain.dto.request.RefreshTokenRequest;
import com.transport.urbain.dto.request.RegisterRequest;
import com.transport.urbain.dto.response.AuthResponse;
import com.transport.urbain.dto.response.JwtResponse;
import com.transport.urbain.dto.response.UserResponse;
import com.transport.urbain.event.UserCreatedEvent;
import com.transport.urbain.event.producer.UserEventProducer;
import com.transport.urbain.exception.DuplicateUserException;
import com.transport.urbain.exception.InvalidCredentialsException;
import com.transport.urbain.exception.TokenExpiredException;
import com.transport.urbain.exception.UserNotFoundException;
import com.transport.urbain.model.*;
import com.transport.urbain.repository.RefreshTokenRepository;
import com.transport.urbain.repository.RoleRepository;
import com.transport.urbain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthServiceImpl.
 * 
 * <p>Tests authentication service implementation including:
 * <ul>
 *   <li>User registration with validation</li>
 *   <li>User login with credential verification</li>
 *   <li>Token refresh operations</li>
 *   <li>Logout and token revocation</li>
 *   <li>Account locking after failed attempts</li>
 * </ul>
 * 
 * <p>Verifies:
 * <ul>
 *   <li>Password encryption</li>
 *   <li>JWT token generation</li>
 *   <li>Refresh token management</li>
 *   <li>Account security features</li>
 *   <li>Event publishing</li>
 *   <li>Exception handling</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private Role testRole;
    private UserProfile testProfile;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .id(1L)
                .name(RoleName.PASSENGER)
                .description("Default passenger role")
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
                .emailVerified(false)
                .phoneVerified(false)
                .authProvider(AuthProvider.LOCAL)
                .roles(new HashSet<>(Set.of(testRole)))
                .build();

        testProfile = UserProfile.builder()
                .id(1L)
                .user(testUser)
                .notificationsEnabled(true)
                .emailNotificationsEnabled(true)
                .smsNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .build();

        testUser.setProfile(testProfile);
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("newuser@example.com")
                .password("Password@123")
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("+1234567891")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(roleRepository.findByName(RoleName.PASSENGER)).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
        when(jwtService.getExpirationTime()).thenReturn(3600L);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(new UserResponse());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).existsByPhoneNumber(request.getPhoneNumber());
        verify(roleRepository).findByName(RoleName.PASSENGER);
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateAccessToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
        verify(userEventProducer).publishUserCreated(any(UserCreatedEvent.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("existing@example.com")
                .password("Password@123")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberAlreadyExists() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("new@example.com")
                .password("Password@123")
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("+1234567890")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining("Phone number already registered");

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).existsByPhoneNumber(request.getPhoneNumber());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password@123")
                .deviceInfo("Test Device")
                .ipAddress("127.0.0.1")
                .build();

        when(userRepository.findByEmailWithRoles(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
        when(jwtService.getExpirationTime()).thenReturn(3600L);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(new UserResponse());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");

        verify(userRepository).findByEmailWithRoles(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), testUser.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateAccessToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("Password@123")
                .build();

        when(userRepository.findByEmailWithRoles(request.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("WrongPassword")
                .build();

        when(userRepository.findByEmailWithRoles(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");

        verify(passwordEncoder).matches(request.getPassword(), testUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenAccountIsLocked() {
        // Given
        testUser.setAccountNonLocked(false);
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password@123")
                .build();

        when(userRepository.findByEmailWithRoles(request.getEmail())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Account is locked");

        verify(userRepository).findByEmailWithRoles(request.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenAccountIsDisabled() {
        // Given
        testUser.setEnabled(false);
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password@123")
                .build();

        when(userRepository.findByEmailWithRoles(request.getEmail())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Account is disabled");

        verify(userRepository).findByEmailWithRoles(request.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        // Given
        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token("validRefreshToken")
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .build();

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("validRefreshToken")
                .build();

        when(refreshTokenRepository.findByToken("validRefreshToken")).thenReturn(Optional.of(refreshToken));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("newAccessToken");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        // When
        JwtResponse response = authService.refreshToken(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);

        verify(refreshTokenRepository).findByToken("validRefreshToken");
        verify(jwtService).generateAccessToken(testUser);
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenNotFound() {
        // Given
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("invalidToken")
                .build();

        when(refreshTokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(TokenExpiredException.class)
                .hasMessageContaining("Refresh token not found");
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsRevoked() {
        // Given
        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token("revokedToken")
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .revoked(true)
                .build();

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("revokedToken")
                .build();

        when(refreshTokenRepository.findByToken("revokedToken")).thenReturn(Optional.of(refreshToken));

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(TokenExpiredException.class)
                .hasMessageContaining("Refresh token has been revoked");
    }

    @Test
    void shouldLogoutSuccessfully() {
        // Given
        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token("validToken")
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("validToken")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // When
        authService.logout("validToken");

        // Then
        verify(refreshTokenRepository).findByToken("validToken");
        verify(refreshTokenRepository).save(refreshToken);
        assertThat(refreshToken.getRevoked()).isTrue();
    }

    @Test
    void shouldRevokeAllUserTokens() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(refreshTokenRepository).revokeAllUserTokens(any(User.class), any(LocalDateTime.class));

        // When
        authService.revokeAllUserTokens(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(refreshTokenRepository).revokeAllUserTokens(eq(testUser), any(LocalDateTime.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForRevokeTokens() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.revokeAllUserTokens(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(1L);
        verify(refreshTokenRepository, never()).revokeAllUserTokens(any(), any());
    }

    @Test
    void shouldLockAccountAfterMaxFailedAttempts() {
        // Given
        testUser.setFailedLoginAttempts(4);
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("WrongPassword")
                .build();

        when(userRepository.findByEmailWithRoles(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(savedUser.getAccountNonLocked()).isFalse();
    }
}

