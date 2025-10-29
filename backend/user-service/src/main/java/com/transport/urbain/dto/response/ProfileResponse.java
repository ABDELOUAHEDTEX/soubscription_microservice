package com.transport.urbain.dto.response;

import com.transport.urbain.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing user profile information.
 * 
 * <p>Contains detailed profile data for a user, including personal information,
 * contact details, preferences, and timestamps. This response is returned when
 * retrieving or updating a user profile.
 * 
 * <p>Profile information includes:
 * <ul>
 *   <li>User and profile identifiers</li>
 *   <li>Personal details: date of birth, gender</li>
 *   <li>Address information: address, city, country, postal code</li>
 *   <li>Additional details: nationality, occupation, bio</li>
 *   <li>Emergency contact information</li>
 *   <li>Notification preferences (email, SMS, push)</li>
 *   <li>Language preferences</li>
 *   <li>Creation and update timestamps</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private Long id;
    private Long userId;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String nationality;
    private String occupation;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String bio;
    private String preferredLanguage;
    private Boolean notificationsEnabled;
    private Boolean emailNotificationsEnabled;
    private Boolean smsNotificationsEnabled;
    private Boolean pushNotificationsEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
