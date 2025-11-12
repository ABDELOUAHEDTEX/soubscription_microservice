package com.transport.subscription.service;

import com.transport.subscription.dto.mapper.SubscriptionMapper;
import com.transport.subscription.dto.request.CancelSubscriptionRequest;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.RenewSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.exception.InvalidSubscriptionException;
import com.transport.subscription.exception.PlanNotFoundException;
import com.transport.subscription.exception.SubscriptionExpiredException;
import com.transport.subscription.exception.SubscriptionNotFoundException;
import com.transport.subscription.model.Plan;
import com.transport.subscription.model.Subscription;
import com.transport.subscription.model.SubscriptionHistory;
import com.transport.subscription.model.SubscriptionStatus;
import com.transport.subscription.repository.PlanRepository;
import com.transport.subscription.repository.SubscriptionHistoryRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import com.transport.subscription.util.DateCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service pour la gestion des abonnements
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final SubscriptionHistoryRepository historyRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final DateCalculator dateCalculator;

    @Override
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        log.info("Création d'un nouvel abonnement pour l'utilisateur: {}", request.getUserId());

        // Vérifier que le plan existe
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new PlanNotFoundException("Plan not found with id: " + request.getPlanId()));

        // Vérifier que le plan est actif
        if (!plan.getIsActive()) {
            throw new InvalidSubscriptionException("Cannot create subscription with inactive plan");
        }

        // Vérifier qu'il n'y a pas déjà un abonnement actif pour ce plan
        if (subscriptionRepository.existsByUserIdAndPlanIdAndStatus(
                request.getUserId(), request.getPlanId(), SubscriptionStatus.ACTIVE)) {
            throw new InvalidSubscriptionException(
                    "User already has an active subscription for this plan");
        }

        // Créer l'abonnement
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = dateCalculator.calculateEndDate(startDate, plan.getDurationDays());
        LocalDate nextBillingDate = endDate;

        Subscription subscription = subscriptionMapper.toEntity(request, plan);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setNextBillingDate(nextBillingDate);
        subscription.setStatus(SubscriptionStatus.PENDING);

        // Générer le QR code (simplifié - à implémenter selon vos besoins)
        subscription.setQrCodeData(generateQrCode(subscription));

        subscription = subscriptionRepository.save(subscription);

        // Enregistrer l'historique (pas d'ancien statut pour une création)
        recordHistory(subscription, null, SubscriptionStatus.PENDING, "SUBSCRIPTION_CREATED", 
                "Subscription created with plan: " + plan.getPlanCode());

        log.info("Abonnement créé avec succès: {}", subscription.getSubscriptionId());
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(UUID subscriptionId) {
        log.debug("Récupération de l'abonnement avec ID: {}", subscriptionId);
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found with id: " + subscriptionId));
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getUserSubscriptions(UUID userId) {
        log.debug("Récupération des abonnements pour l'utilisateur: {}", userId);
        List<Subscription> subscriptions = subscriptionRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
        return subscriptionMapper.toResponseList(subscriptions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getActiveUserSubscriptions(UUID userId) {
        log.debug("Récupération des abonnements actifs pour l'utilisateur: {}", userId);
        List<Subscription> subscriptions = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
        return subscriptionMapper.toResponseList(subscriptions);
    }

    @Override
    public SubscriptionResponse updateSubscription(UUID subscriptionId, UpdateSubscriptionRequest request) {
        log.info("Mise à jour de l'abonnement: {}", subscriptionId);
        
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found with id: " + subscriptionId));

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscriptionMapper.updateFromRequest(request, subscription);
        subscription = subscriptionRepository.save(subscription);

        recordHistory(subscription, oldStatus, subscription.getStatus(), "SUBSCRIPTION_UPDATED", 
                "Subscription updated");

        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public SubscriptionResponse cancelSubscription(UUID subscriptionId, CancelSubscriptionRequest request) {
        log.info("Annulation de l'abonnement: {}", subscriptionId);
        
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found with id: " + subscriptionId));

        if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new SubscriptionExpiredException("Cannot cancel an expired subscription");
        }

        SubscriptionStatus oldStatus = subscription.getStatus();
        
        if (Boolean.TRUE.equals(request.getImmediate())) {
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscription.setEndDate(LocalDate.now());
        } else {
            // Annulation à la fin de la période
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscription.setAutoRenewEnabled(false);
        }

        subscription = subscriptionRepository.save(subscription);

        recordHistory(subscription, oldStatus, SubscriptionStatus.CANCELLED, "SUBSCRIPTION_CANCELLED", 
                "Subscription cancelled. Reason: " + (request.getReason() != null ? request.getReason() : "No reason provided"));

        log.info("Abonnement annulé: {}", subscriptionId);
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public SubscriptionResponse renewSubscription(UUID subscriptionId, RenewSubscriptionRequest request) {
        log.info("Renouvellement de l'abonnement: {}", subscriptionId);
        
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found with id: " + subscriptionId));

        if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new SubscriptionExpiredException("Cannot renew an expired subscription");
        }

        // Si un nouveau plan est spécifié, le changer
        if (request.getPlanId() != null) {
            Plan newPlan = planRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new PlanNotFoundException("Plan not found with id: " + request.getPlanId()));
            subscription.setPlan(newPlan);
        }

        // Mettre à jour les dates
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = dateCalculator.calculateEndDate(
                newStartDate, subscription.getPlan().getDurationDays());
        subscription.setStartDate(newStartDate);
        subscription.setEndDate(newEndDate);
        subscription.setNextBillingDate(newEndDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        if (request.getCardToken() != null) {
            subscription.setCardToken(request.getCardToken());
        }

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscription = subscriptionRepository.save(subscription);

        recordHistory(subscription, oldStatus, SubscriptionStatus.ACTIVE, "SUBSCRIPTION_RENEWED", 
                "Subscription renewed");

        log.info("Abonnement renouvelé: {}", subscriptionId);
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public SubscriptionResponse activateSubscription(UUID subscriptionId) {
        log.info("Activation de l'abonnement: {}", subscriptionId);
        
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found with id: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.PENDING) {
            throw new InvalidSubscriptionException(
                    "Only PENDING subscriptions can be activated. Current status: " + subscription.getStatus());
        }

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription = subscriptionRepository.save(subscription);

        recordHistory(subscription, oldStatus, SubscriptionStatus.ACTIVE, "SUBSCRIPTION_ACTIVATED", 
                "Subscription activated after successful payment");

        log.info("Abonnement activé: {}", subscriptionId);
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public SubscriptionResponse expireSubscription(UUID subscriptionId) {
        log.info("Expiration de l'abonnement: {}", subscriptionId);
        
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found with id: " + subscriptionId));

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription = subscriptionRepository.save(subscription);

        recordHistory(subscription, oldStatus, SubscriptionStatus.EXPIRED, "SUBSCRIPTION_EXPIRED", 
                "Subscription expired");

        log.info("Abonnement expiré: {}", subscriptionId);
        return subscriptionMapper.toResponse(subscription);
    }

    /**
     * Enregistre un événement dans l'historique
     */
    private void recordHistory(Subscription subscription, SubscriptionStatus oldStatus, 
                              SubscriptionStatus newStatus, String eventType, String details) {
        SubscriptionHistory history = SubscriptionHistory.builder()
                .subscription(subscription)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .eventType(eventType)
                .details(details)
                .build();
        historyRepository.save(history);
    }

    /**
     * Génère un QR code pour l'abonnement (simplifié)
     * TODO: Implémenter la génération réelle de QR code
     */
    private String generateQrCode(Subscription subscription) {
        // Format simplifié: SUBSCRIPTION_ID|USER_ID|PLAN_CODE
        return String.format("%s|%s|%s", 
                subscription.getSubscriptionId(), 
                subscription.getUserId(),
                subscription.getPlan().getPlanCode());
    }
}

