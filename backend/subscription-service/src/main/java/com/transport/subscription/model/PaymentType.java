package com.transport.subscription.model;

/**
 * Type de paiement
 */
public enum PaymentType {
    INITIAL,     // Paiement initial
    RENEWAL,     // Renouvellement
    UPGRADE,     // Mise à niveau
    DOWNGRADE,   // Rétrogradation
    ADJUSTMENT,  // Ajustement
    REFUND       // Remboursement
}

