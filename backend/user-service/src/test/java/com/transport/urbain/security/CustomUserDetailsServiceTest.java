package com.transport.urbain.security;

import com.transport.urbain.model.*;
import com.transport.urbain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the CustomUserDetailsService.
 * 
 * <p>Tests Spring Security UserDetailsService implementation including:
 * <ul>
 *   <li>User loading by username (email)</li>
 *   <li>Authority (role) conversion</li>
 *   <li>Account status handling (enabled, locked)</li>
 *   <li>Multiple role support</li>
 * </ul>
 * 
 * <p>Verifies:
 * <ul>
 *   <li>User details retrieval with roles</li>
 *   <li>Proper GrantedAuthority creation</li>
 *   <li>Account status flags</li>
 *   <li>Exception handling for non-existent users</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;
    private Role passengerRole;
    private Role driverRole;

    @BeforeEach
    void setUp() {
        passengerRole = Role.builder()
                .id(1L)
                .name(RoleName.PASSENGER)
                .description("Passenger role")
                .build();

        driverRole = Role.builder()
                .id(2L)
                .name(RoleName.DRIVER)
                .description("Driver role")
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
                .phoneVerified(true)
                .authProvider(AuthProvider.LOCAL)
                .roles(new HashSet<>(Set.of(passengerRole)))
                .build();

        UserProfile testProfile = UserProfile.builder()
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
    void shouldLoadUserByUsernameSuccessfully() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();

        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());
        
        assertThat(authorities).containsExactly("PASSENGER");
    }

    @Test
    void shouldLoadUserWithMultipleRoles() {
        // Given
        testUser.setRoles(new HashSet<>(Set.of(passengerRole, driverRole)));
        String email = "test@example.com";
        when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Then
        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());
        
        assertThat(authorities).containsExactlyInAnyOrder("PASSENGER", "DRIVER");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String email = "notfound@example.com";
        when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: " + email);
    }

    @Test
    void shouldHandleDisabledAccount() {
        // Given
        testUser.setEnabled(false);
        String email = "test@example.com";
        when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Then
        assertThat(userDetails.isEnabled()).isFalse();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void shouldHandleLockedAccount() {
        // Given
        testUser.setAccountNonLocked(false);
        String email = "test@example.com";
        when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Then
        assertThat(userDetails.isAccountNonLocked()).isFalse();
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
    }
}

