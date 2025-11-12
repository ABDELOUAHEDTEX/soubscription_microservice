package com.transport.subscription.service;

import com.transport.subscription.dto.mapper.PlanMapper;
import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.exception.PlanNotFoundException;
import com.transport.subscription.model.Plan;
import com.transport.subscription.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service pour la gestion des plans d'abonnement
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    @Override
    public List<PlanResponse> getAllActivePlans() {
        log.debug("Récupération de tous les plans actifs");
        List<Plan> plans = planRepository.findByIsActive(true);
        log.info("{} plan(s) actif(s) trouvé(s)", plans.size());
        return planMapper.toResponseList(plans);
    }

    @Override
    public PlanResponse getPlanById(UUID planId) {
        log.debug("Récupération du plan avec ID: {}", planId);
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> {
                    log.error("Plan non trouvé avec l'ID: {}", planId);
                    return new PlanNotFoundException("Plan not found with id: " + planId);
                });
        log.debug("Plan trouvé: {} ({})", plan.getPlanCode(), plan.getPlanId());
        return planMapper.toResponse(plan);
    }

    @Override
    public PlanResponse getPlanByCode(String planCode) {
        log.debug("Récupération du plan avec code: {}", planCode);
        Plan plan = planRepository.findByPlanCode(planCode)
                .orElseThrow(() -> {
                    log.error("Plan non trouvé avec le code: {}", planCode);
                    return new PlanNotFoundException("Plan not found with code: " + planCode);
                });
        log.debug("Plan trouvé: {} ({})", plan.getPlanCode(), plan.getPlanId());
        return planMapper.toResponse(plan);
    }

    @Override
    public List<PlanResponse> getAllPlans() {
        log.debug("Récupération de tous les plans");
        List<Plan> plans = planRepository.findAll();
        log.info("{} plan(s) trouvé(s) au total", plans.size());
        return planMapper.toResponseList(plans);
    }
}

