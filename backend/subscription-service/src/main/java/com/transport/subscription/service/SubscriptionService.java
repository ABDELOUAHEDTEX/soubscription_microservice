package com.transport.subscription.service;

import com.transport.subscription.dto.request.CancelSubscriptionRequest;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.RenewSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.model.SubscriptionStatus;

import java.util.List;
import java.util.UUID;

/**
 * Interface du service pour la gestion des abonnements
 */
public interface SubscriptionService {

    /**
     * Crée un nouvel abonnement
     * @param request Données de création
     * @return Abonnement créé
     * @throws com.transport.subscription.exception.PlanNotFoundException si le plan n'existe pas
     * @throws com.transport.subscription.exception.InvalidSubscriptionException si l'utilisateur a déjà un abonnement actif
     */
    SubscriptionResponse createSubscription(CreateSubscriptionRequest request);

    /**
     * Récupère un abonnement par son ID
     * @param subscriptionId ID de l'abonnement
     * @return Abonnement trouvé
     * @throws com.transport.subscription.exception.SubscriptionNotFoundException si l'abonnement n'existe pas
     */
    SubscriptionResponse getSubscriptionById(UUID subscriptionId);

    /**
     * Récupère tous les abonnements d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des abonnements
     */
    List<SubscriptionResponse> getUserSubscriptions(UUID userId);

    /**
     * Récupère les abonnements actifs d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des abonnements actifs
     */
    List<SubscriptionResponse> getActiveUserSubscriptions(UUID userId);

    /**
     * Met à jour un abonnement
     * @param subscriptionId ID de l'abonnement
     * @param request Données de mise à jour
     * @return Abonnement mis à jour
     * @throws com.transport.subscription.exception.SubscriptionNotFoundException si l'abonnement n'existe pas
     */
    SubscriptionResponse updateSubscription(UUID subscriptionId, UpdateSubscriptionRequest request);

    /**
     * Annule un abonnement
     * @param subscriptionId ID de l'abonnement
     * @param request Données d'annulation
     * @return Abonnement annulé
     * @throws com.transport.subscription.exception.SubscriptionNotFoundException si l'abonnement n'existe pas
     * @throws com.transport.subscription.exception.SubscriptionExpiredException si l'abonnement est déjà expiré
     */
    SubscriptionResponse cancelSubscription(UUID subscriptionId, CancelSubscriptionRequest request);

    /**
     * Renouvelle un abonnement
     * @param subscriptionId ID de l'abonnement
     * @param request Données de renouvellement
     * @return Abonnement renouvelé
     * @throws com.transport.subscription.exception.SubscriptionNotFoundException si l'abonnement n'existe pas
     * @throws com.transport.subscription.exception.SubscriptionExpiredException si l'abonnement est expiré
     */
    SubscriptionResponse renewSubscription(UUID subscriptionId, RenewSubscriptionRequest request);

    /**
     * Active un abonnement (appelé après paiement réussi)
     * @param subscriptionId ID de l'abonnement
     * @return Abonnement activé
     * @throws com.transport.subscription.exception.SubscriptionNotFoundException si l'abonnement n'existe pas
     */
    SubscriptionResponse activateSubscription(UUID subscriptionId);

    /**
     * Marque un abonnement comme expiré
     * @param subscriptionId ID de l'abonnement
     * @return Abonnement expiré
     * @throws com.transport.subscription.exception.SubscriptionNotFoundException si l'abonnement n'existe pas
     */
    SubscriptionResponse expireSubscription(UUID subscriptionId);
}

