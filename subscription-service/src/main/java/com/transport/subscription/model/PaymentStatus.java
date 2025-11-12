package com.transport.subscription.model;

/**
 * Statut d'un paiement
 */
public enum PaymentStatus {
    PENDING,    // En attente
    SUCCEEDED,   // Réussi
    FAILED,      // Échoué
    REFUNDED,    // Remboursé
    CANCELLED    // Annulé
}

