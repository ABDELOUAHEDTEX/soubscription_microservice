package com.transport.urbain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 * 
 * <p>Represents the request data required for user authentication.
 * Contains the user's email and password, along with optional device
 * tracking information for security purposes.
 * 
 * <p>The email must be a valid email address format and both email
 * and password are required fields.
 * 
 * <p>Optional fields include device information and IP address for
 * auditing and security monitoring.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String deviceInfo;
    private String ipAddress;
}
