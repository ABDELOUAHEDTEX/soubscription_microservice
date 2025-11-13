package com.transport.subscription.scheduler;

import com.transport.subscription.service.RenewalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Scheduler pour vérifier et expirer les abonnements
 * Exécute des tâches planifiées pour marquer les abonnements expirés
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExpirationCheckScheduler {

    private final RenewalService renewalService;

    /**
     * Vérifie et expire les abonnements tous les jours à 3h00 du matin
     * Cron: second minute hour day month weekday
     * 0 0 3 * * * = Tous les jours à 3h00
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void expireSubscriptions() {
        log.info("=== Démarrage de la vérification des abonnements expirés ===");
        
        try {
            LocalDate today = LocalDate.now();
            int expiredCount = renewalService.expireSubscriptions(today);
            
            if (expiredCount > 0) {
                log.info("✅ {} abonnement(s) expiré(s)", expiredCount);
            } else {
                log.info("ℹ️ Aucun abonnement à expirer aujourd'hui");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification des abonnements expirés", e);
        }
        
        log.info("=== Fin de la vérification des abonnements expirés ===");
    }

    /**
     * Vérifie et expire les abonnements toutes les heures (pour les tests)
     * Désactiver en production et utiliser la méthode ci-dessus
     */
    // @Scheduled(fixedRate = 3600000) // Toutes les heures (en millisecondes)
    public void expireSubscriptionsHourly() {
        log.debug("Vérification horaire des abonnements expirés");
        expireSubscriptions();
    }
}

