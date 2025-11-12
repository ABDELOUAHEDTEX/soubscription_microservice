package com.transport.subscription.repository;

import com.transport.subscription.model.SubscriptionPayment;
import com.transport.subscription.model.PaymentStatus;
import com.transport.subscription.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour l'entité SubscriptionPayment (historique de facturation)
 * Fournit les opérations CRUD et les méthodes de recherche personnalisées
 */
@Repository
public interface BillingHistoryRepository extends JpaRepository<SubscriptionPayment, UUID> {

    /**
     * Trouve tous les paiements d'un abonnement
     * @param subscriptionId ID de l'abonnement
     * @return Liste des paiements, triés par date décroissante
     */
    List<SubscriptionPayment> findBySubscriptionIdOrderByPaymentDateDesc(UUID subscriptionId);

    /**
     * Trouve les paiements d'un abonnement par statut
     * @param subscriptionId ID de l'abonnement
     * @param paymentStatus Statut du paiement
     * @return Liste des paiements
     */
    List<SubscriptionPayment> findBySubscriptionIdAndPaymentStatus(
            UUID subscriptionId, 
            PaymentStatus paymentStatus
    );

    /**
     * Trouve un paiement par sa clé d'idempotence
     * @param idempotencyKey Clé d'idempotence
     * @return Paiement trouvé ou Optional.empty()
     */
    Optional<SubscriptionPayment> findByIdempotencyKey(String idempotencyKey);

    /**
     * Vérifie si un paiement existe par sa clé d'idempotence
     * @param idempotencyKey Clé d'idempotence
     * @return true si le paiement existe
     */
    boolean existsByIdempotencyKey(String idempotencyKey);

    /**
     * Trouve tous les paiements par statut
     * @param paymentStatus Statut du paiement
     * @return Liste des paiements
     */
    List<SubscriptionPayment> findByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Trouve les paiements échoués
     * @param paymentStatus Statut échoué
     * @return Liste des paiements échoués
     */
    List<SubscriptionPayment> findByPaymentStatusOrderByPaymentDateDesc(PaymentStatus paymentStatus);

    /**
     * Trouve les paiements par type
     * @param paymentType Type de paiement
     * @return Liste des paiements
     */
    List<SubscriptionPayment> findByPaymentType(PaymentType paymentType);

    /**
     * Trouve les paiements dans une période donnée
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Liste des paiements
     */
    @Query("SELECT p FROM SubscriptionPayment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<SubscriptionPayment> findPaymentsBetweenDates(
            @Param("startDate") OffsetDateTime startDate, 
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Calcule le montant total des paiements réussis d'un abonnement
     * @param subscriptionId ID de l'abonnement
     * @param paymentStatus Statut réussi
     * @return Montant total
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM SubscriptionPayment p " +
           "WHERE p.subscription.subscriptionId = :subscriptionId AND p.paymentStatus = :paymentStatus")
    Double calculateTotalPaidAmount(
            @Param("subscriptionId") UUID subscriptionId, 
            @Param("paymentStatus") PaymentStatus paymentStatus
    );

    /**
     * Trouve un paiement par ID de transaction externe
     * @param externalTxnId ID de transaction externe
     * @return Paiement trouvé ou Optional.empty()
     */
    Optional<SubscriptionPayment> findByExternalTxnId(String externalTxnId);
}

