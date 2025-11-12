package com.transport.subscription.service;

import com.transport.subscription.dto.request.RenewSubscriptionRequest;
import com.transport.subscription.model.Subscription;
import com.transport.subscription.model.SubscriptionStatus;
import com.transport.subscription.repository.SubscriptionRepository;
import com.transport.subscription.util.DateCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service pour la gestion du renouvellement automatique
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RenewalServiceImpl implements RenewalService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final DateCalculator dateCalculator;

    @Override
    public int processAutomaticRenewals(LocalDate today) {
        log.info("Traitement des renouvellements automatiques pour la date: {}", today);

        // Récupérer les abonnements actifs avec renouvellement automatique activé
        List<Subscription> subscriptionsToRenew = subscriptionRepository
                .findByStatusAndAutoRenewEnabled(SubscriptionStatus.ACTIVE, true);

        int renewedCount = 0;

        for (Subscription subscription : subscriptionsToRenew) {
            // Vérifier si la date de facturation est atteinte ou dépassée
            if (subscription.getNextBillingDate() != null &&
                !subscription.getNextBillingDate().isAfter(today)) {
                
                try {
                    log.info("Renouvellement automatique de l'abonnement: {}", 
                            subscription.getSubscriptionId());
                    
                    // Renouveler l'abonnement (sans changer de plan)
                    subscriptionService.renewSubscription(
                            subscription.getSubscriptionId(),
                            RenewSubscriptionRequest.builder()
                                    .build() // Pas de nouveau plan, utilise le plan actuel
                    );
                    
                    renewedCount++;
                } catch (Exception e) {
                    log.error("Erreur lors du renouvellement automatique de l'abonnement: {}", 
                            subscription.getSubscriptionId(), e);
                }
            }
        }

        log.info("{} abonnements renouvelés automatiquement", renewedCount);
        return renewedCount;
    }

    @Override
    public int expireSubscriptions(LocalDate today) {
        log.info("Expiration des abonnements pour la date: {}", today);

        // Récupérer les abonnements actifs expirés
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findExpiredSubscriptions(SubscriptionStatus.ACTIVE, today);

        int expiredCount = 0;

        for (Subscription subscription : expiredSubscriptions) {
            try {
                log.info("Expiration de l'abonnement: {}", subscription.getSubscriptionId());
                subscriptionService.expireSubscription(subscription.getSubscriptionId());
                expiredCount++;
            } catch (Exception e) {
                log.error("Erreur lors de l'expiration de l'abonnement: {}", 
                        subscription.getSubscriptionId(), e);
            }
        }

        log.info("{} abonnements expirés", expiredCount);
        return expiredCount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getSubscriptionsToRenew(LocalDate today) {
        log.debug("Récupération de la liste des abonnements à renouveler pour: {}", today);

        List<Subscription> subscriptions = subscriptionRepository
                .findByStatusAndNextBillingDateLessThanEqual(
                        SubscriptionStatus.ACTIVE, today);

        return subscriptions.stream()
                .filter(sub -> Boolean.TRUE.equals(sub.getAutoRenewEnabled()))
                .map(Subscription::getSubscriptionId)
                .collect(Collectors.toList());
    }
}

