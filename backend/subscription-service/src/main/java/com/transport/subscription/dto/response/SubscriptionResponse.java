package com.transport.subscription.dto.response;

import com.transport.subscription.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour un abonnement
 * Expose les informations de l'abonnement sans les détails sensibles
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private UUID subscriptionId;
    private UUID userId;
    
    // Informations du plan (simplifiées)
    private UUID planId;
    private String planCode;
    
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;
    private BigDecimal amountPaid;
    private Boolean autoRenewEnabled;
    
    // Informations de carte (masquées pour la sécurité)
    private String cardLastFour; // Derniers 4 chiffres seulement
    private Integer cardExpMonth;
    private Integer cardExpYear;
    
    // QR Code (si disponible)
    private String qrCodeData;
    
    // Timestamps
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

