package com.transport.urbain.security;

import com.transport.urbain.model.User;
import com.transport.urbain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom implementation of UserDetailsService for Spring Security.
 * 
 * <p>This service loads user details by email (username) and converts them
 * into a Spring Security UserDetails object with authorities (roles).
 * 
 * <p>Features:
 * <ul>
 *   <li>Loads user by email with roles eagerly fetched</li>
 *   <li>Converts user roles to Spring Security authorities</li>
 *   <li>Handles user account status (enabled, locked)</li>
 *   <li>Transactional to ensure data consistency</li>
 * </ul>
 * 
 * <p>The UserDetails returned includes:
 * <ul>
 *   <li>Username (email)</li>
 *   <li>Password (hashed)</li>
 *   <li>Enabled status</li>
 *   <li>Account non-expired (always true)</li>
 *   <li>Credentials non-expired (always true)</li>
 *   <li>Account non-locked</li>
 *   <li>Authorities (roles converted to GrantedAuthority)</li>
 * </ul>
 * 
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see org.springframework.security.core.userdetails.UserDetails
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user details by username (email) from the database.
     * 
     * <p>Fetches the user with roles and converts to Spring Security UserDetails.
     * Uses eager loading to avoid N+1 query problems.
     * 
     * @param email the email address of the user to load
     * @return UserDetails containing user information and authorities
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getEnabled(),
                true,
                true,
                user.getAccountNonLocked(),
                getAuthorities(user)
        );
    }

    /**
     * Converts user roles to Spring Security GrantedAuthority objects.
     * 
     * <p>Each role name is prefixed with "ROLE_" to follow Spring Security conventions.
     * 
     * @param user the user whose roles to convert
     * @return a collection of GrantedAuthority objects representing the user's roles
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }
}
