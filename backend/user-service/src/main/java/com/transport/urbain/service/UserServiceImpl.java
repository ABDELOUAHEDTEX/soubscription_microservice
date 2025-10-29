package com.transport.urbain.service;

import com.transport.urbain.dto.mapper.ProfileMapper;
import com.transport.urbain.dto.mapper.UserMapper;
import com.transport.urbain.dto.request.ChangePasswordRequest;
import com.transport.urbain.dto.request.UpdateProfileRequest;
import com.transport.urbain.dto.response.ProfileResponse;
import com.transport.urbain.dto.response.UserResponse;
import com.transport.urbain.event.UserDeletedEvent;
import com.transport.urbain.event.UserUpdatedEvent;
import com.transport.urbain.event.producer.UserEventProducer;
import com.transport.urbain.exception.InvalidCredentialsException;
import com.transport.urbain.exception.UserNotFoundException;
import com.transport.urbain.model.User;
import com.transport.urbain.model.UserProfile;
import com.transport.urbain.model.UserStatus;
import com.transport.urbain.repository.UserProfileRepository;
import com.transport.urbain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of user management service.
 * 
 * <p>Handles user CRUD operations, profile management, and administrative functions.
 * Provides comprehensive user management including:
 * <ul>
 *   <li>User retrieval by ID, email, or search</li>
 *   <li>User update and deletion (soft delete)</li>
 *   <li>Profile retrieval and updates</li>
 *   <li>Password change with validation</li>
 *   <li>Email and phone verification</li>
 *   <li>Account unlocking</li>
 * </ul>
 * 
 * <p>Event publishing:
 * <ul>
 *   <li>UserUpdatedEvent - Published when user information is updated</li>
 *   <li>UserDeletedEvent - Published when user account is deleted</li>
 * </ul>
 * 
 * <p>Features:
 * <ul>
 *   <li>Partial profile updates (only non-null fields)</li>
 *   <li>Password validation before allowing changes</li>
 *   <li>Soft deletion (marks as DELETED status)</li>
 *   <li>Automated event publishing for integration</li>
 * </ul>
 * 
 * @see com.transport.urbain.service.UserService
 * @see com.transport.urbain.event.producer.UserEventProducer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toUserResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    public Page<UserResponse> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        user = userRepository.save(user);

        // Publish user updated event
        userEventProducer.publishUserUpdated(new UserUpdatedEvent(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                LocalDateTime.now()
        ));

        log.info("User updated successfully: {}", user.getEmail());

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Verify new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("New password and confirm password do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setStatus(UserStatus.DELETED);
        user.setEnabled(false);
        userRepository.save(user);

        // Publish user deleted event
        userEventProducer.publishUserDeleted(new UserDeletedEvent(
                user.getId(),
                user.getEmail(),
                LocalDateTime.now()
        ));

        log.info("User deleted successfully: {}", user.getEmail());
    }

    @Override
    public ProfileResponse getUserProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User profile not found"));
        return profileMapper.toProfileResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateUserProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = UserProfile.builder()
                    .user(user)
                    .build();
        }

        // Update profile fields
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }
        if (request.getPostalCode() != null) {
            profile.setPostalCode(request.getPostalCode());
        }
        if (request.getNationality() != null) {
            profile.setNationality(request.getNationality());
        }
        if (request.getOccupation() != null) {
            profile.setOccupation(request.getOccupation());
        }
        if (request.getEmergencyContactName() != null) {
            profile.setEmergencyContactName(request.getEmergencyContactName());
        }
        if (request.getEmergencyContactPhone() != null) {
            profile.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getPreferredLanguage() != null) {
            profile.setPreferredLanguage(request.getPreferredLanguage());
        }
        if (request.getNotificationsEnabled() != null) {
            profile.setNotificationsEnabled(request.getNotificationsEnabled());
        }
        if (request.getEmailNotificationsEnabled() != null) {
            profile.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        }
        if (request.getSmsNotificationsEnabled() != null) {
            profile.setSmsNotificationsEnabled(request.getSmsNotificationsEnabled());
        }
        if (request.getPushNotificationsEnabled() != null) {
            profile.setPushNotificationsEnabled(request.getPushNotificationsEnabled());
        }

        profile = userProfileRepository.save(profile);

        log.info("User profile updated successfully for user: {}", user.getEmail());

        return profileMapper.toProfileResponse(profile);
    }

    @Override
    @Transactional
    public void verifyEmail(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // TODO: Implement email verification logic with token validation
        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("Email verified for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void verifyPhone(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // TODO: Implement phone verification logic with code validation
        user.setPhoneVerified(true);
        userRepository.save(user);

        log.info("Phone verified for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void unlockAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.unlock();
        userRepository.save(user);

        log.info("Account unlocked for user: {}", user.getEmail());
    }
}
