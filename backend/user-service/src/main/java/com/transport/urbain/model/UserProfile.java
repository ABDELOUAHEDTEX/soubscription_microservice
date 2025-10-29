package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing extended profile information for a user.
 * 
 * <p>This entity stores detailed personal information, contact details,
 * preferences, and settings that are not part of the core User entity.
 * Each user has exactly one profile (one-to-one relationship).
 * 
 * <p>Profile information includes:
 * <ul>
 *   <li>Personal details: date of birth, gender, bio</li>
 *   <li>Address information: address, city, country, postal code</li>
 *   <li>Contact details: occupation, nationality</li>
 *   <li>Emergency contact information</li>
 *   <li>Notification preferences</li>
 *   <li>Language preferences</li>
 * </ul>
 * 
 * <p>Notification settings:
 * <ul>
 *   <li>Global notifications toggle</li>
 *   <li>Email notifications</li>
 *   <li>SMS notifications</li>
 *   <li>Push notifications</li>
 * </ul>
 * 
 * @see User
 * @see Gender
 */
@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String country;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 50)
    private String nationality;

    @Column(length = 100)
    private String occupation;

    @Column(length = 20)
    private String emergencyContactName;

    @Column(length = 20)
    private String emergencyContactPhone;

    @Column(length = 1000)
    private String bio;

    @Column(length = 10)
    private String preferredLanguage;

    @Column(nullable = false)
    @Builder.Default
    private Boolean notificationsEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailNotificationsEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean smsNotificationsEnabled = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean pushNotificationsEnabled = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
