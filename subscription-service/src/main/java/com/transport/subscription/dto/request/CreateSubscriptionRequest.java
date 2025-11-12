package com.transport.subscription.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO pour créer un nouvel abonnement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Plan ID is required")
    private UUID planId;

    private Boolean autoRenewEnabled;

    // Informations de carte (optionnel, peut être fourni plus tard)
    private String cardToken;
    private Integer cardExpMonth;
    private Integer cardExpYear;
}

