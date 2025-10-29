package com.transport.urbain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration requests.
 * 
 * <p>Represents the request data required to register a new user account.
 * Includes validation to ensure data quality and security:
 * <ul>
 *   <li>Email must be a valid email address</li>
 *   <li>Password must be between 8 and 100 characters with specific complexity requirements</li>
 *   <li>First and last names must be between 2 and 100 characters</li>
 *   <li>Phone number must follow E.164 format if provided</li>
 * </ul>
 * 
 * <p>Password requirements:
 * <ul>
 *   <li>At least one digit</li>
 *   <li>At least one lowercase letter</li>
 *   <li>At least one uppercase letter</li>
 *   <li>At least one special character (@#$%^&+=)</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be valid")
    private String phoneNumber;
}
