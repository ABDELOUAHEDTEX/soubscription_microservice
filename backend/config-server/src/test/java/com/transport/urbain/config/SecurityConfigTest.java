package com.transport.urbain.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SecurityConfig}.
 * <p>
 * Tests verify security configuration, password encoding, user details service,
 * and security filter chain setup.
 */
@DisplayName("Security Configuration Tests")
class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
        passwordEncoder = securityConfig.passwordEncoder();
    }

    @Test
    @DisplayName("Should create BCrypt password encoder")
    void testPasswordEncoderCreation() {
        // When
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // Then
        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    }

    @Test
    @DisplayName("Should encode passwords with BCrypt")
    void testPasswordEncoding() {
        // Given
        String rawPassword = "config123";

        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"));
    }

    @Test
    @DisplayName("Should match correct password after encoding")
    void testPasswordMatching() {
        // Given
        String rawPassword = "config123";

        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrongpassword", encodedPassword));
    }

    @Test
    @DisplayName("Should generate different encodings for same password")
    void testPasswordEncodingUniqueness() {
        // Given
        String rawPassword = "config123";

        // When
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);

        // Then
        assertNotEquals(encoded1, encoded2, "BCrypt should generate different salts");
        assertTrue(passwordEncoder.matches(rawPassword, encoded1));
        assertTrue(passwordEncoder.matches(rawPassword, encoded2));
    }

    @Test
    @DisplayName("Should create user details service")
    void testUserDetailsServiceCreation() {
        // When
        UserDetailsService service = securityConfig.userDetailsService();

        // Then
        assertNotNull(service);
    }

    @Test
    @DisplayName("Should load default config user")
    void testLoadConfigUser() {
        // Given
        UserDetailsService service = securityConfig.userDetailsService();

        // When
        UserDetails user = service.loadUserByUsername("config");

        // Then
        assertNotNull(user);
        assertEquals("config", user.getUsername());
        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isEmpty());
    }

    @Test
    @DisplayName("Should have ADMIN role for config user")
    void testConfigUserHasAdminRole() {
        // Given
        UserDetailsService service = securityConfig.userDetailsService();

        // When
        UserDetails user = service.loadUserByUsername("config");

        // Then
        assertTrue(user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should throw exception for unknown user")
    void testUnknownUser() {
        // Given
        UserDetailsService service = securityConfig.userDetailsService();

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("unknown");
        });
    }

    @Test
    @DisplayName("Should have password that doesn't match wrong password")
    void testWrongPasswordDoesNotMatch() {
        // Given
        UserDetailsService service = securityConfig.userDetailsService();
        UserDetails user = service.loadUserByUsername("config");

        // When
        boolean matches = passwordEncoder.matches("wrongpassword", user.getPassword());

        // Then
        assertFalse(matches);
    }

    @Test
    @DisplayName("Should have password that matches correct password")
    void testCorrectPasswordMatches() {
        // Given
        UserDetailsService service = securityConfig.userDetailsService();
        UserDetails user = service.loadUserByUsername("config");

        // When
        boolean matches = passwordEncoder.matches("config123", user.getPassword());

        // Then
        assertTrue(matches);
    }

    @Test
    @DisplayName("Should have account not expired")
    void testAccountNotExpired() {
        // Given
        UserDetailsService service = securityConfig.userDetailsService();
        UserDetails user = service.loadUserByUsername("config");

        // Then
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    @DisplayName("Should have account not locked")
    void testAccountNotLocked() {
        // Given
        UserDetailsService service = securityConfig.userDetailsService();
        UserDetails user = service.loadUserByUsername("config");

        // Then
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should have credentials not expired")
    void testCredentialsNotExpired() {
        // Given
        UserDetailsService service = securityConfig.userDetailsService();
        UserDetails user = service.loadUserByUsername("config");

        // Then
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Should have user enabled")
    void testUserEnabled() {
        // Given
        UserDetailsService service = securityConfig.userDetailsService();
        UserDetails user = service.loadUserByUsername("config");

        // Then
        assertTrue(user.isEnabled());
    }
}

