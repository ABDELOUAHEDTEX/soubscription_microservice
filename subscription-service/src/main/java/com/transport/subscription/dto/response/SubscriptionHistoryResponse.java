package com.transport.subscription.dto.response;

import com.transport.subscription.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO de réponse pour un événement d'historique d'abonnement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHistoryResponse {

    private UUID historyId;
    private UUID subscriptionId;
    private SubscriptionStatus oldStatus;
    private SubscriptionStatus newStatus;
    private String eventType;
    private OffsetDateTime eventDate;
    private UUID performedBy;
    private String details;
    private Map<String, Object> metadata;
}

