package com.transport.subscription.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.model.PaymentMethod;
import com.transport.subscription.model.Plan;
import com.transport.subscription.model.Subscription;
import com.transport.subscription.model.SubscriptionStatus;
import com.transport.subscription.repository.BillingHistoryRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour BillingController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("BillingController Integration Tests")
class BillingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private BillingHistoryRepository billingHistoryRepository;

    private Plan plan;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        billingHistoryRepository.deleteAll();
        subscriptionRepository.deleteAll();
        planRepository.deleteAll();

        plan = Plan.builder()
                .planCode("MONTHLY_TEST")
                .description("Plan mensuel test")
                .durationDays(30)
                .price(new BigDecimal("19.99"))
                .currency("EUR")
                .isActive(true)
                .build();
        plan = planRepository.save(plan);

        subscription = Subscription.builder()
                .userId(UUID.randomUUID())
                .plan(plan)
                .status(SubscriptionStatus.PENDING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .nextBillingDate(LocalDate.now().plusDays(30))
                .amountPaid(BigDecimal.ZERO)
                .autoRenewEnabled(true)
                .build();
        subscription = subscriptionRepository.save(subscription);
    }

    @Test
    @DisplayName("POST /api/subscriptions/billing/payments - Devrait traiter un paiement")
    void shouldProcessPayment() throws Exception {
        performPayment();
    }

    @Test
    @DisplayName("GET /api/subscriptions/billing/payments/{id} - Devrait retourner 404 si le paiement n'existe pas")
    void shouldReturn404WhenPaymentNotFound() throws Exception {
        mockMvc.perform(get("/api/subscriptions/billing/payments/{id}", UUID.randomUUID()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/subscriptions/billing/subscriptions/{id} - Devrait retourner la liste des paiements")
    void shouldReturnBillingHistory() throws Exception {
        performPayment();

        mockMvc.perform(get("/api/subscriptions/billing/subscriptions/{id}", subscription.getSubscriptionId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/subscriptions/billing/subscriptions/{id}/total - Devrait retourner le montant total payé")
    void shouldReturnTotalPaidAmount() throws Exception {
        performPayment();

        mockMvc.perform(get("/api/subscriptions/billing/subscriptions/{id}/total", subscription.getSubscriptionId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(plan.getPrice().toString()));
    }

    private void performPayment() throws Exception {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .amount(plan.getPrice())
                .currency(plan.getCurrency())
                .paymentMethod(PaymentMethod.CARD)
                .cardToken("tok_integration")
                .idempotencyKey("idemp-integration")
                .build();

        mockMvc.perform(post("/api/subscriptions/billing/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subscriptionId").value(subscription.getSubscriptionId().toString()))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCEEDED"));
    }
}
