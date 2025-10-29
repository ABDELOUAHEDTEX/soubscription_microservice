package com.transport.urbain.service;

import com.transport.urbain.model.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the JwtServiceImpl.
 * 
 * <p>Tests JWT token service implementation including:
 * <ul>
 *   <li>Access token generation with claims</li>
 *   <li>Refresh token generation</li>
 *   <li>Token validation</li>
 *   <li>Claim extraction (username, userId, roles)</li>
 *   <li>Token expiration checking</li>
 * </ul>
 * 
 * <p>Verifies:
 * <ul>
 *   <li>Correct token structure</li>
 *   <li>Claims presence and values</li>
 *   <li>Token signing and verification</li>
 *   <li>Expiration handling</li>
 *   <li>Role inclusion in tokens</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    private User testUser;
    private Role testRole;
    
    private static final String SECRET = "mySecretKey12345678901234567890123456789012345678901234567890"; // 64 characters

    @BeforeEach
    void setUp() {
        // Set up the secret and expiration values using reflection
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L); // 7 days

        testRole = Role.builder()
                .id(1L)
                .name(RoleName.PASSENGER)
                .description("Default passenger role")
                .build();

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .status(UserStatus.ACTIVE)
                .enabled(true)
                .accountNonLocked(true)
                .authProvider(AuthProvider.LOCAL)
                .roles(roles)
                .build();
    }

    @Test
    void shouldGenerateAccessTokenSuccessfully() {
        // When
        String token = jwtService.generateAccessToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // Verify token structure
        Claims claims = extractClaims(token);
        assertThat(claims.getSubject()).isEqualTo(testUser.getEmail());
        assertThat(((Number) claims.get("userId")).longValue()).isEqualTo(testUser.getId());
        assertThat(claims.get("email")).isEqualTo(testUser.getEmail());
        assertThat(claims.get("roles")).isNotNull();
    }

    @Test
    void shouldGenerateRefreshTokenSuccessfully() {
        // When
        String token = jwtService.generateRefreshToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // Verify token structure
        Claims claims = extractClaims(token);
        assertThat(claims.getSubject()).isEqualTo(testUser.getEmail());
        assertThat(((Number) claims.get("userId")).longValue()).isEqualTo(testUser.getId());
        assertThat(claims.get("type")).isEqualTo("refresh");
    }

    @Test
    void shouldExtractUsernameFromToken() {
        // Given
        String token = jwtService.generateAccessToken(testUser);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertThat(username).isEqualTo(testUser.getEmail());
    }

    @Test
    void shouldExtractAllClaimsFromToken() {
        // Given
        String token = jwtService.generateAccessToken(testUser);

        // When
        Claims claims = jwtService.extractAllClaims(token);

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(testUser.getEmail());
        assertThat(((Number) claims.get("userId")).longValue()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        // Given
        String token = jwtService.generateAccessToken(testUser);

        // When
        boolean isValid = jwtService.isTokenValid(token, testUser.getEmail());

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldReturnFalseWhenTokenIsInvalid() {
        // Given
        String token = jwtService.generateAccessToken(testUser);

        // When
        boolean isValid = jwtService.isTokenValid(token, "different@example.com");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldNotBeExpiredImmediatelyAfterCreation() {
        // Given
        String token = jwtService.generateAccessToken(testUser);

        // When
        boolean isExpired = jwtService.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    void shouldReturnExpirationTime() {
        // When
        Long expirationTime = jwtService.getExpirationTime();

        // Then
        assertThat(expirationTime).isEqualTo(3600000L);
    }

    @Test
    void shouldGenerateValidTokens() {
        // When
        String token = jwtService.generateAccessToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        
        // Verify it's a valid token
        Claims claims = extractClaims(token);
        assertThat(claims.getSubject()).isEqualTo(testUser.getEmail());
    }

    @Test
    void shouldIncludeUserRolesInToken() {
        // Given
        String token = jwtService.generateAccessToken(testUser);

        // When
        Claims claims = extractClaims(token);

        // Then
        assertThat(claims.get("roles")).isNotNull();
    }

    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getBody();
    }
}

