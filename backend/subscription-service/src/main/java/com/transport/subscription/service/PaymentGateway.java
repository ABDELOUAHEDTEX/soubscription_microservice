package com.transport.subscription.service;

import com.transport.subscription.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Abstraction d'une passerelle de paiement (Stripe, PayPal, etc.)
 */
public interface PaymentGateway {

    /**
     * Traite un paiement auprès du fournisseur externe.
     * @param request informations de paiement
     * @return résultat du paiement
     */
    PaymentResult charge(PaymentRequest request);

    /**
     * Effectue un remboursement auprès du fournisseur externe.
     * @param externalTransactionId identifiant de transaction externe
     * @param amount montant à rembourser
     * @return résultat du remboursement
     */
    RefundResult refund(String externalTransactionId, BigDecimal amount);

    /**
     * Vérifie la signature d'un webhook provenant du fournisseur.
     * @param payload payload brut
     * @param signature signature fournie
     * @return true si valide, false sinon
     */
    boolean verifyWebhookSignature(String payload, String signature);

    /**
     * Représente une requête de paiement envoyée à la passerelle.
     */
    record PaymentRequest(
            UUID subscriptionId,
            UUID userId,
            BigDecimal amount,
            String currency,
            PaymentMethod paymentMethod,
            String cardToken,
            String idempotencyKey
    ) { }

    /**
     * Résultat d'un paiement auprès de la passerelle.
     */
    record PaymentResult(
            boolean success,
            String externalTransactionId,
            String failureReason
    ) { }

    /**
     * Résultat d'un remboursement.
     */
    record RefundResult(
            boolean success,
            String externalTransactionId,
            String failureReason
    ) { }
}
