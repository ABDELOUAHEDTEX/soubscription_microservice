package com.transport.subscription.scheduler;

import com.transport.subscription.service.RenewalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Scheduler pour le renouvellement automatique des abonnements
 * Exécute des tâches planifiées pour renouveler les abonnements arrivant à échéance
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionRenewalScheduler {

    private final RenewalService renewalService;

    /**
     * Traite les renouvellements automatiques tous les jours à 2h00 du matin
     * Cron: second minute hour day month weekday
     * 0 0 2 * * * = Tous les jours à 2h00
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void processAutomaticRenewals() {
        log.info("=== Démarrage du traitement des renouvellements automatiques ===");
        
        try {
            LocalDate today = LocalDate.now();
            int renewedCount = renewalService.processAutomaticRenewals(today);
            
            if (renewedCount > 0) {
                log.info("✅ {} abonnement(s) renouvelé(s) automatiquement", renewedCount);
            } else {
                log.info("ℹ️ Aucun abonnement à renouveler aujourd'hui");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors du traitement des renouvellements automatiques", e);
        }
        
        log.info("=== Fin du traitement des renouvellements automatiques ===");
    }

    /**
     * Traite les renouvellements automatiques toutes les heures (pour les tests)
     * Désactiver en production et utiliser la méthode ci-dessus
     */
    // @Scheduled(fixedRate = 3600000) // Toutes les heures (en millisecondes)
    public void processAutomaticRenewalsHourly() {
        log.debug("Traitement horaire des renouvellements automatiques");
        processAutomaticRenewals();
    }
}

