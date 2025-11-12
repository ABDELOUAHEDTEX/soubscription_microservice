package com.transport.subscription.service;

import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service en charge du traitement des paiements d'abonnement.
 */
public interface PaymentService {

    /**
     * Traite un paiement pour un abonnement.
     * @param request données de la requête de paiement
     * @return réponse contenant les informations du paiement
     */
    PaymentResponse processPayment(ProcessPaymentRequest request);

    /**
     * Demande un remboursement pour un paiement.
     * @param paymentId identifiant du paiement
     * @return paiement remboursé
     */
    PaymentResponse refundPayment(UUID paymentId);

    /**
     * Récupère tous les paiements pour un abonnement donné.
     * @param subscriptionId identifiant de l'abonnement
     * @return liste des paiements
     */
    List<PaymentResponse> getPaymentsBySubscriptionId(UUID subscriptionId);

    /**
     * Récupère les informations d'un paiement spécifique.
     * @param paymentId identifiant du paiement
     * @return paiement
     */
    PaymentResponse getPaymentById(UUID paymentId);
}
