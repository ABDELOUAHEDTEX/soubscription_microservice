package com.transport.subscription.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour annuler un abonnement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelSubscriptionRequest {

    /**
     * Raison de l'annulation (optionnel)
     */
    private String reason;

    /**
     * Indique si l'annulation doit être immédiate ou à la fin de la période
     */
    private Boolean immediate;
}

