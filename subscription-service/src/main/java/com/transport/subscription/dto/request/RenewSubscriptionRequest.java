package com.transport.subscription.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO pour renouveler un abonnement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenewSubscriptionRequest {

    /**
     * ID du plan pour le renouvellement (optionnel, utilise le plan actuel si null)
     */
    private UUID planId;

    /**
     * Token de carte mis à jour (optionnel)
     */
    private String cardToken;
}

