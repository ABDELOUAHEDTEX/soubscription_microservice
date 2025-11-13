package com.transport.subscription.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.model.Plan;
import com.transport.subscription.model.Subscription;
import com.transport.subscription.model.SubscriptionStatus;
import com.transport.subscription.repository.PlanRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour SubscriptionController
 * Teste les endpoints REST avec une base de données en mémoire
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("SubscriptionController Integration Tests")
class SubscriptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private Plan testPlan;
    private UUID userId;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données
        subscriptionRepository.deleteAll();
        planRepository.deleteAll();

        // Créer un plan de test
        testPlan = Plan.builder()
                .planCode("TEST_MONTHLY")
                .description("Plan de test mensuel")
                .durationDays(30)
                .price(new BigDecimal("29.99"))
                .currency("EUR")
                .isActive(true)
                .build();
        testPlan = planRepository.save(testPlan);

        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("POST /api/subscriptions - Devrait créer un abonnement avec succès")
    void shouldCreateSubscription() throws Exception {
        // Given
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .userId(userId)
                .planId(testPlan.getPlanId())
                .autoRenewEnabled(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subscriptionId").exists())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.planId").value(testPlan.getPlanId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/subscriptions - Devrait retourner 400 si le plan n'existe pas")
    void shouldReturn400WhenPlanNotFound() throws Exception {
        // Given
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .userId(userId)
                .planId(UUID.randomUUID()) // Plan inexistant
                .build();

        // When & Then
        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("POST /api/subscriptions - Devrait retourner 400 si userId est manquant")
    void shouldReturn400WhenUserIdMissing() throws Exception {
        // Given
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .planId(testPlan.getPlanId())
                .build();

        // When & Then
        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/subscriptions/{id} - Devrait retourner un abonnement")
    void shouldGetSubscriptionById() throws Exception {
        // Given
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .plan(testPlan)
                .status(SubscriptionStatus.PENDING)
                .startDate(LocalDate.now())
                .amountPaid(BigDecimal.ZERO)
                .autoRenewEnabled(true)
                .build();
        subscription = subscriptionRepository.save(subscription);

        // When & Then
        mockMvc.perform(get("/api/subscriptions/{id}", subscription.getSubscriptionId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(subscription.getSubscriptionId().toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    @DisplayName("GET /api/subscriptions/{id} - Devrait retourner 404 si l'abonnement n'existe pas")
    void shouldReturn404WhenSubscriptionNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/subscriptions/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/subscriptions/users/{userId} - Devrait retourner les abonnements d'un utilisateur")
    void shouldGetUserSubscriptions() throws Exception {
        // Given
        Subscription subscription1 = Subscription.builder()
                .userId(userId)
                .plan(testPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .amountPaid(BigDecimal.ZERO)
                .autoRenewEnabled(true)
                .build();
        subscriptionRepository.save(subscription1);

        Subscription subscription2 = Subscription.builder()
                .userId(userId)
                .plan(testPlan)
                .status(SubscriptionStatus.PENDING)
                .startDate(LocalDate.now())
                .amountPaid(BigDecimal.ZERO)
                .autoRenewEnabled(true)
                .build();
        subscriptionRepository.save(subscription2);

        // When & Then
        mockMvc.perform(get("/api/subscriptions/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }

    @Test
    @DisplayName("POST /api/subscriptions/{id}/activate - Devrait activer un abonnement")
    void shouldActivateSubscription() throws Exception {
        // Given
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .plan(testPlan)
                .status(SubscriptionStatus.PENDING)
                .startDate(LocalDate.now())
                .amountPaid(BigDecimal.ZERO)
                .autoRenewEnabled(true)
                .build();
        subscription = subscriptionRepository.save(subscription);

        // When & Then
        mockMvc.perform(post("/api/subscriptions/{id}/activate", subscription.getSubscriptionId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}

