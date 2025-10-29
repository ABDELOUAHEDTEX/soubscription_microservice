package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.UserMapper;
import com.transport.urbain.dto.request.LoginRequest;
import com.transport.urbain.dto.request.RefreshTokenRequest;
import com.transport.urbain.dto.request.RegisterRequest;
import com.transport.urbain.dto.response.AuthResponse;
import com.transport.urbain.dto.response.JwtResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of authentication service.
 * 
 * <p>Handles user registration, login, token refresh, and logout operations.
 * Implements security measures including:
 * <ul>
 *   <li>Account locking after multiple failed login attempts</li>
 *   <li>Password encryption using BCrypt</li>
 *   <li>JWT token generation and validation</li>
 *   <li>Refresh token management and revocation</li>
 *   <li>User event publishing for microservices integration</li>
 * </ul>
 * 
 * <p>Security features:
 * <ul>
 *   <li>Maximum failed attempts: 5 before account lock</li>
 *   <li>Token expiration tracking and validation</li>
 *   <li>Device and IP address tracking for audit purposes</li>
 * </ul>
 * 
 * <p>Event publishing:
 * <ul>
 *   <li>UserCreatedEvent - Published after successful registration</li>
 * </ul>
 * 
 * @see com.transport.urbain.service.AuthService
 * @see com.transport.urbain.event.producer.UserEventProducer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserEventProducer userEventProducer;

    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("Email already registered: " + request.getEmail());
        }

        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateUserException("Phone number already registered: " + request.getPhoneNumber());
        }

        // Get default role
        Role userRole = roleRepository.findByName(RoleName.PASSENGER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .status(UserStatus.ACTIVE)
                .roles(roles)
                .emailVerified(false)
                .phoneVerified(false)
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .authProvider(AuthProvider.LOCAL)
                .build();

        // Create default profile
        UserProfile profile = UserProfile.builder()
                .user(user)
                .notificationsEnabled(true)
                .emailNotificationsEnabled(true)
                .smsNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .build();

        user.setProfile(profile);

        user = userRepository.save(user);

        // Publish user created event
        userEventProducer.publishUserCreated(new UserCreatedEvent(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                LocalDateTime.now()
        ));

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save refresh token
        saveRefreshToken(user, refreshToken, null, null);

        log.info("User registered successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .user(userMapper.toUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getEmail());

        User user = userRepository.findByEmailWithRoles(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Check if account is locked
        if (!user.getAccountNonLocked()) {
            throw new InvalidCredentialsException("Account is locked due to multiple failed login attempts");
        }

        // Check if account is enabled
        if (!user.getEnabled()) {
            throw new InvalidCredentialsException("Account is disabled");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Reset failed attempts on successful login
        if (user.getFailedLoginAttempts() > 0) {
            user.resetFailedAttempts();
        }

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save refresh token
        saveRefreshToken(user, refreshToken, request.getDeviceInfo(), request.getIpAddress());

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .user(userMapper.toUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing access token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new TokenExpiredException("Refresh token not found"));

        if (refreshToken.getRevoked()) {
            throw new TokenExpiredException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            throw new TokenExpiredException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);

        log.info("Access token refreshed for user: {}", user.getEmail());

        return JwtResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        log.info("User logout");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenExpiredException("Refresh token not found"));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        log.info("User logged out successfully");
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());

        log.info("All tokens revoked for user: {}", user.getEmail());
    }

    private void handleFailedLogin(User user) {
        user.incrementFailedAttempts();

        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.lock();
            log.warn("User account locked due to multiple failed login attempts: {}", user.getEmail());
        }

        userRepository.save(user);
    }

    private void saveRefreshToken(User user, String token, String deviceInfo, String ipAddress) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
