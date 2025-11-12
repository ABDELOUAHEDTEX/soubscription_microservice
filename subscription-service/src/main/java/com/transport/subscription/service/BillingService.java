package com.transport.subscription.service;

import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Interface du service pour la gestion de la facturation
 */
public interface BillingService {

    /**
     * Récupère l'historique de facturation d'un abonnement
     * @param subscriptionId ID de l'abonnement
     * @return Liste des paiements
     */
    List<PaymentResponse> getBillingHistory(UUID subscriptionId);

    /**
     * Récupère un paiement par son ID
     * @param paymentId ID du paiement
     * @return Paiement trouvé
     * @throws com.transport.subscription.exception.SubscriptionNotFoundException si le paiement n'existe pas
     */
    PaymentResponse getPaymentById(UUID paymentId);

    /**
     * Enregistre un paiement réussi
     * @param subscriptionId ID de l'abonnement
     * @param amount Montant payé
     * @param currency Devise
     * @param externalTxnId ID de transaction externe
     * @param idempotencyKey Clé d'idempotence
     * @param paymentMethod Méthode de paiement utilisée
     * @return Paiement enregistré
     */
    PaymentResponse recordSuccessfulPayment(
            UUID subscriptionId,
            BigDecimal amount,
            String currency,
            String externalTxnId,
            String idempotencyKey,
            PaymentMethod paymentMethod
    );

    /**
     * Enregistre un paiement échoué
     * @param subscriptionId ID de l'abonnement
     * @param amount Montant
     * @param currency Devise
     * @param failureReason Raison de l'échec
     * @param idempotencyKey Clé d'idempotence
     * @param paymentMethod Méthode de paiement utilisée
     * @return Paiement enregistré
     */
    PaymentResponse recordFailedPayment(
            UUID subscriptionId,
            BigDecimal amount,
            String currency,
            String failureReason,
            String idempotencyKey,
            PaymentMethod paymentMethod
    );

    /**
     * Calcule le montant total payé pour un abonnement
     * @param subscriptionId ID de l'abonnement
     * @return Montant total payé
     */
    BigDecimal getTotalPaidAmount(UUID subscriptionId);
}

