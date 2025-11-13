package com.transport.subscription.service;

import com.transport.subscription.dto.response.PlanResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface du service pour la gestion des plans d'abonnement
 */
public interface PlanService {

    /**
     * Récupère tous les plans actifs
     * @return Liste des plans actifs
     */
    List<PlanResponse> getAllActivePlans();

    /**
     * Récupère un plan par son ID
     * @param planId ID du plan
     * @return Plan trouvé
     * @throws com.transport.subscription.exception.PlanNotFoundException si le plan n'existe pas
     */
    PlanResponse getPlanById(UUID planId);

    /**
     * Récupère un plan par son code
     * @param planCode Code du plan
     * @return Plan trouvé
     * @throws com.transport.subscription.exception.PlanNotFoundException si le plan n'existe pas
     */
    PlanResponse getPlanByCode(String planCode);

    /**
     * Récupère tous les plans (actifs et inactifs)
     * @return Liste de tous les plans
     */
    List<PlanResponse> getAllPlans();
}

