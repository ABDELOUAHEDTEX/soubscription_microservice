package com.transport.subscription.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Utilitaire pour les calculs de dates liés aux abonnements
 */
@Component
@Slf4j
public class DateCalculator {

    /**
     * Calcule la date de fin d'un abonnement à partir de la date de début et de la durée
     * @param startDate Date de début
     * @param durationDays Durée en jours
     * @return Date de fin
     */
    public LocalDate calculateEndDate(LocalDate startDate, Integer durationDays) {
        log.debug("Calcul de la date de fin: startDate={}, durationDays={}", startDate, durationDays);
        LocalDate endDate = startDate.plusDays(durationDays);
        log.debug("Date de fin calculée: {}", endDate);
        return endDate;
    }

    /**
     * Vérifie si une date est dans le passé
     * @param date Date à vérifier
     * @return true si la date est dans le passé
     */
    public boolean isPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Vérifie si une date est dans le futur
     * @param date Date à vérifier
     * @return true si la date est dans le futur
     */
    public boolean isFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }
}

