package com.transport.subscription.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour un plan d'abonnement
 * Expose uniquement les informations publiques du plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {

    private UUID planId;
    private String planCode;
    private String description;
    private Integer durationDays;
    private BigDecimal price;
    private String currency;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

