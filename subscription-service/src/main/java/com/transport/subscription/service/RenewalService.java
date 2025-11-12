package com.transport.subscription.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Interface du service pour la gestion du renouvellement automatique
 */
public interface RenewalService {

    /**
     * Traite les renouvellements automatiques des abonnements qui arrivent à échéance
     * @param today Date du jour
     * @return Nombre d'abonnements renouvelés
     */
    int processAutomaticRenewals(LocalDate today);

    /**
     * Vérifie et expire les abonnements qui ont dépassé leur date de fin
     * @param today Date du jour
     * @return Nombre d'abonnements expirés
     */
    int expireSubscriptions(LocalDate today);

    /**
     * Récupère la liste des abonnements à renouveler
     * @param today Date du jour
     * @return Liste des IDs d'abonnements à renouveler
     */
    List<UUID> getSubscriptionsToRenew(LocalDate today);
}

