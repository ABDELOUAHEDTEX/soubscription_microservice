package com.transport.subscription.repository;

import com.transport.subscription.model.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository pour l'entité SubscriptionHistory
 * Fournit les opérations CRUD et les méthodes de recherche personnalisées
 */
@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, UUID> {

    /**
     * Trouve tout l'historique d'un abonnement
     * @param subscriptionId ID de l'abonnement
     * @return Liste des événements historiques, triés par date décroissante
     */
    List<SubscriptionHistory> findBySubscriptionIdOrderByEventDateDesc(UUID subscriptionId);

    /**
     * Trouve l'historique d'un abonnement par type d'événement
     * @param subscriptionId ID de l'abonnement
     * @param eventType Type d'événement
     * @return Liste des événements
     */
    List<SubscriptionHistory> findBySubscriptionIdAndEventType(
            UUID subscriptionId, 
            String eventType
    );

    /**
     * Trouve les événements historiques dans une période donnée
     * @param subscriptionId ID de l'abonnement
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Liste des événements
     */
    @Query("SELECT h FROM SubscriptionHistory h WHERE h.subscription.subscriptionId = :subscriptionId " +
           "AND h.eventDate BETWEEN :startDate AND :endDate ORDER BY h.eventDate DESC")
    List<SubscriptionHistory> findHistoryBetweenDates(
            @Param("subscriptionId") UUID subscriptionId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Trouve les événements effectués par un utilisateur
     * @param performedBy ID de l'utilisateur qui a effectué l'action
     * @return Liste des événements
     */
    List<SubscriptionHistory> findByPerformedByOrderByEventDateDesc(UUID performedBy);

    /**
     * Trouve le dernier événement d'un abonnement
     * @param subscriptionId ID de l'abonnement
     * @return Dernier événement ou null
     */
    SubscriptionHistory findFirstBySubscriptionIdOrderByEventDateDesc(UUID subscriptionId);
}

