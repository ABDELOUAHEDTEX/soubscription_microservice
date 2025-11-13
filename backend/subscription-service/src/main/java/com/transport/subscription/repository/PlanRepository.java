package com.transport.subscription.repository;

import com.transport.subscription.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour l'entité Plan
 * Fournit les opérations CRUD et les méthodes de recherche personnalisées
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {

    /**
     * Trouve un plan par son code unique
     * @param planCode Code du plan
     * @return Plan trouvé ou Optional.empty()
     */
    Optional<Plan> findByPlanCode(String planCode);

    /**
     * Vérifie si un plan existe par son code
     * @param planCode Code du plan
     * @return true si le plan existe
     */
    boolean existsByPlanCode(String planCode);

    /**
     * Trouve tous les plans actifs
     * @param isActive Statut actif
     * @return Liste des plans actifs
     */
    List<Plan> findByIsActive(Boolean isActive);

    /**
     * Trouve tous les plans actifs, triés par prix croissant
     * @param isActive Statut actif
     * @return Liste des plans actifs triés par prix
     */
    List<Plan> findByIsActiveOrderByPriceAsc(Boolean isActive);
}

