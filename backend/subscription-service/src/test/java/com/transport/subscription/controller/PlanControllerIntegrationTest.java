package com.transport.subscription.controller;

import com.transport.subscription.model.Plan;
import com.transport.subscription.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour PlanController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("PlanController Integration Tests")
class PlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanRepository planRepository;

    private Plan activePlan;
    private Plan inactivePlan;

    @BeforeEach
    void setUp() {
        planRepository.deleteAll();

        activePlan = Plan.builder()
                .planCode("ACTIVE_PLAN")
                .description("Plan actif")
                .durationDays(30)
                .price(new BigDecimal("29.99"))
                .currency("EUR")
                .isActive(true)
                .build();
        activePlan = planRepository.save(activePlan);

        inactivePlan = Plan.builder()
                .planCode("INACTIVE_PLAN")
                .description("Plan inactif")
                .durationDays(60)
                .price(new BigDecimal("49.99"))
                .currency("EUR")
                .isActive(false)
                .build();
        inactivePlan = planRepository.save(inactivePlan);
    }

    @Test
    @DisplayName("GET /api/subscriptions/plans - Devrait retourner uniquement les plans actifs")
    void shouldReturnOnlyActivePlans() throws Exception {
        mockMvc.perform(get("/api/subscriptions/plans"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].planCode").value("ACTIVE_PLAN"))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    @DisplayName("GET /api/subscriptions/plans/all - Devrait retourner tous les plans")
    void shouldReturnAllPlans() throws Exception {
        mockMvc.perform(get("/api/subscriptions/plans/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/subscriptions/plans/{id} - Devrait retourner un plan par ID")
    void shouldGetPlanById() throws Exception {
        mockMvc.perform(get("/api/subscriptions/plans/{id}", activePlan.getPlanId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planId").value(activePlan.getPlanId().toString()))
                .andExpect(jsonPath("$.planCode").value("ACTIVE_PLAN"));
    }

    @Test
    @DisplayName("GET /api/subscriptions/plans/{id} - Devrait retourner 404 si le plan n'existe pas")
    void shouldReturn404WhenPlanNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/subscriptions/plans/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("GET /api/subscriptions/plans/code/{code} - Devrait retourner un plan par code")
    void shouldGetPlanByCode() throws Exception {
        mockMvc.perform(get("/api/subscriptions/plans/code/{code}", "ACTIVE_PLAN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planCode").value("ACTIVE_PLAN"));
    }
}

