package com.transport.subscription.dto.response;

import com.transport.subscription.model.PaymentMethod;
import com.transport.subscription.model.PaymentStatus;
import com.transport.subscription.model.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour un paiement (historique de facturation)
 * Expose les informations de paiement sans les détails sensibles
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID paymentId;
    private UUID subscriptionId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private PaymentType paymentType;
    private OffsetDateTime paymentDate;
    private String failureReason; // Exposé pour le debug, mais peut être masqué en production
    private String externalTxnId;
    private OffsetDateTime createdAt;
    private String idempotencyKey;
}
