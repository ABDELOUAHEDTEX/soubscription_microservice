package com.transport.urbain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for refresh token requests.
 * 
 * <p>Represents the request data required to refresh an access token.
 * Contains the refresh token obtained during initial authentication.
 * 
 * <p>The refresh token is used to obtain a new access token when the
 * current one expires, without requiring the user to log in again.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
