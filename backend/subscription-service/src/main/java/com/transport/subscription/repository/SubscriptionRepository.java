package com.transport.subscription.repository;

import com.transport.subscription.model.Subscription;
import com.transport.subscription.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour l'entité Subscription
 * Fournit les opérations CRUD et les méthodes de recherche personnalisées
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    /**
     * Trouve toutes les abonnements d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des abonnements, triés par date de création décroissante
     */
    List<Subscription> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Trouve les abonnements d'un utilisateur par statut
     * @param userId ID de l'utilisateur
     * @param status Statut de l'abonnement
     * @return Liste des abonnements
     */
    List<Subscription> findByUserIdAndStatus(UUID userId, SubscriptionStatus status);

    /**
     * Trouve l'abonnement actif d'un utilisateur pour un plan donné
     * @param userId ID de l'utilisateur
     * @param planId ID du plan
     * @param status Statut actif
     * @return Abonnement actif ou Optional.empty()
     */
    Optional<Subscription> findByUserIdAndPlanIdAndStatus(
            UUID userId, 
            UUID planId, 
            SubscriptionStatus status
    );

    /**
     * Vérifie si un utilisateur a un abonnement actif pour un plan donné
     * @param userId ID de l'utilisateur
     * @param planId ID du plan
     * @param status Statut actif
     * @return true si un abonnement actif existe
     */
    boolean existsByUserIdAndPlanIdAndStatus(
            UUID userId, 
            UUID planId, 
            SubscriptionStatus status
    );

    /**
     * Trouve tous les abonnements par statut
     * @param status Statut de l'abonnement
     * @return Liste des abonnements
     */
    List<Subscription> findByStatus(SubscriptionStatus status);

    /**
     * Trouve les abonnements qui expirent bientôt (pour renouvellement automatique)
     * @param status Statut actif
     * @param nextBillingDate Date de facturation prochaine
     * @return Liste des abonnements à renouveler
     */
    List<Subscription> findByStatusAndNextBillingDateLessThanEqual(
            SubscriptionStatus status, 
            LocalDate nextBillingDate
    );

    /**
     * Trouve les abonnements avec renouvellement automatique activé
     * @param status Statut actif
     * @param autoRenewEnabled Renouvellement automatique activé
     * @return Liste des abonnements
     */
    List<Subscription> findByStatusAndAutoRenewEnabled(
            SubscriptionStatus status, 
            Boolean autoRenewEnabled
    );

    /**
     * Trouve les abonnements expirés (end_date < aujourd'hui)
     * @param status Statut actif
     * @param today Date du jour
     * @return Liste des abonnements expirés
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = :status AND s.endDate < :today")
    List<Subscription> findExpiredSubscriptions(
            @Param("status") SubscriptionStatus status, 
            @Param("today") LocalDate today
    );

    /**
     * Compte le nombre d'abonnements actifs d'un utilisateur
     * @param userId ID de l'utilisateur
     * @param status Statut actif
     * @return Nombre d'abonnements actifs
     */
    long countByUserIdAndStatus(UUID userId, SubscriptionStatus status);

    /**
     * Trouve les abonnements non supprimés (soft delete)
     * @param userId ID de l'utilisateur
     * @return Liste des abonnements non supprimés
     */
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.deletedAt IS NULL")
    List<Subscription> findActiveSubscriptionsByUserId(@Param("userId") UUID userId);
}

