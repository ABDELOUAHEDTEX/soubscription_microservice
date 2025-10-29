package com.transport.urbain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for password change requests.
 * 
 * <p>Represents the request data required to change a user's password.
 * Includes validation to ensure the new password meets security requirements:
 * <ul>
 *   <li>Minimum 8 characters and maximum 100 characters</li>
 *   <li>Must contain at least one digit</li>
 *   <li>Must contain at least one lowercase letter</li>
 *   <li>Must contain at least one uppercase letter</li>
 *   <li>Must contain at least one special character (@#$%^&+=)</li>
 * </ul>
 * 
 * <p>The confirm password field must match the new password for successful validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}