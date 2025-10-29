package com.transport.urbain.dto.request;

import com.transport.urbain.model.Gender;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for updating user profile information.
 * 
 * <p>Represents the request data used to update user profile details.
 * All fields are optional, allowing partial updates of the profile.
 * 
 * <p>Profile information includes:
 * <ul>
 *   <li>Personal information: name, phone, date of birth, gender</li>
 *   <li>Address information: address, city, country, postal code</li>
 *   <li>Additional details: nationality, occupation, bio</li>
 *   <li>Emergency contact information</li>
 *   <li>Notification preferences</li>
 *   <li>Language preferences</li>
 * </ul>
 * 
 * <p>Validation:
 * <ul>
 *   <li>Names must be between 2 and 100 characters</li>
 *   <li>Bio must not exceed 1000 characters</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @Size(min = 2, max = 100)
    private String firstName;

    @Size(min = 2, max = 100)
    private String lastName;

    private String phoneNumber;

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

    @Size(max = 1000)
    private String bio;

    private String preferredLanguage;

    private Boolean notificationsEnabled;

    private Boolean emailNotificationsEnabled;

    private Boolean smsNotificationsEnabled;

    private Boolean pushNotificationsEnabled;
}
