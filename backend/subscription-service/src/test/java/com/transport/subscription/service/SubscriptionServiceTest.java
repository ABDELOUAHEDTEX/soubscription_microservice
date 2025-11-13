package com.transport.subscription.service;

import com.transport.subscription.dto.mapper.SubscriptionMapper;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.exception.InvalidSubscriptionException;
import com.transport.subscription.exception.PlanNotFoundException;
import com.transport.subscription.exception.SubscriptionNotFoundException;
import com.transport.subscription.model.Plan;
import com.transport.subscription.model.Subscription;
import com.transport.subscription.model.SubscriptionStatus;
import com.transport.subscription.repository.PlanRepository;
import com.transport.subscription.repository.SubscriptionHistoryRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import com.transport.subscription.util.DateCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour SubscriptionService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionService Tests")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private SubscriptionHistoryRepository historyRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private DateCalculator dateCalculator;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private UUID userId;
    private UUID planId;
    private UUID subscriptionId;
    private Plan testPlan;
    private Subscription testSubscription;
    private CreateSubscriptionRequest createRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        planId = UUID.randomUUID();
        subscriptionId = UUID.randomUUID();

        testPlan = Plan.builder()
                .planId(planId)
                .planCode("MONTHLY_BASIC")
                .durationDays(30)
                .price(new BigDecimal("29.99"))
                .currency("EUR")
                .isActive(true)
                .build();

        testSubscription = Subscription.builder()
                .subscriptionId(subscriptionId)
                .userId(userId)
                .plan(testPlan)
                .status(SubscriptionStatus.PENDING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .amountPaid(BigDecimal.ZERO)
                .autoRenewEnabled(true)
                .build();

        createRequest = CreateSubscriptionRequest.builder()
                .userId(userId)
                .planId(planId)
                .autoRenewEnabled(true)
                .build();
    }

    @Test
    @DisplayName("Devrait créer un abonnement avec succès")
    void shouldCreateSubscriptionSuccessfully() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);
        
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(subscriptionRepository.existsByUserIdAndPlanIdAndStatus(
                userId, planId, SubscriptionStatus.ACTIVE)).thenReturn(false);
        when(dateCalculator.calculateEndDate(today, 30)).thenReturn(endDate);
        when(subscriptionMapper.toEntity(createRequest, testPlan)).thenReturn(testSubscription);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);
        when(subscriptionMapper.toResponse(testSubscription)).thenReturn(
                SubscriptionResponse.builder()
                        .subscriptionId(subscriptionId)
                        .userId(userId)
                        .status(SubscriptionStatus.PENDING)
                        .build()
        );

        // When
        SubscriptionResponse result = subscriptionService.createSubscription(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(subscriptionId, result.getSubscriptionId());
        verify(planRepository).findById(planId);
        verify(subscriptionRepository).existsByUserIdAndPlanIdAndStatus(
                userId, planId, SubscriptionStatus.ACTIVE);
        verify(subscriptionRepository).save(any(Subscription.class));
        verify(historyRepository).save(any());
    }

    @Test
    @DisplayName("Devrait lever une exception si le plan n'existe pas")
    void shouldThrowExceptionWhenPlanNotFound() {
        // Given
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PlanNotFoundException.class, () -> {
            subscriptionService.createSubscription(createRequest);
        });
        verify(planRepository).findById(planId);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Devrait lever une exception si le plan est inactif")
    void shouldThrowExceptionWhenPlanIsInactive() {
        // Given
        Plan inactivePlan = Plan.builder()
                .planId(planId)
                .isActive(false)
                .build();
        when(planRepository.findById(planId)).thenReturn(Optional.of(inactivePlan));

        // When & Then
        assertThrows(InvalidSubscriptionException.class, () -> {
            subscriptionService.createSubscription(createRequest);
        });
    }

    @Test
    @DisplayName("Devrait lever une exception si l'utilisateur a déjà un abonnement actif")
    void shouldThrowExceptionWhenUserHasActiveSubscription() {
        // Given
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(subscriptionRepository.existsByUserIdAndPlanIdAndStatus(
                userId, planId, SubscriptionStatus.ACTIVE)).thenReturn(true);

        // When & Then
        assertThrows(InvalidSubscriptionException.class, () -> {
            subscriptionService.createSubscription(createRequest);
        });
    }

    @Test
    @DisplayName("Devrait retourner un abonnement par ID")
    void shouldReturnSubscriptionById() {
        // Given
        when(subscriptionRepository.findById(subscriptionId))
                .thenReturn(Optional.of(testSubscription));
        when(subscriptionMapper.toResponse(testSubscription)).thenReturn(
                SubscriptionResponse.builder()
                        .subscriptionId(subscriptionId)
                        .build()
        );

        // When
        SubscriptionResponse result = subscriptionService.getSubscriptionById(subscriptionId);

        // Then
        assertNotNull(result);
        assertEquals(subscriptionId, result.getSubscriptionId());
        verify(subscriptionRepository).findById(subscriptionId);
    }

    @Test
    @DisplayName("Devrait lever une exception si l'abonnement n'existe pas")
    void shouldThrowExceptionWhenSubscriptionNotFound() {
        // Given
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubscriptionNotFoundException.class, () -> {
            subscriptionService.getSubscriptionById(subscriptionId);
        });
    }

    @Test
    @DisplayName("Devrait activer un abonnement avec succès")
    void shouldActivateSubscriptionSuccessfully() {
        // Given
        Subscription pendingSubscription = Subscription.builder()
                .subscriptionId(subscriptionId)
                .status(SubscriptionStatus.PENDING)
                .build();
        
        when(subscriptionRepository.findById(subscriptionId))
                .thenReturn(Optional.of(pendingSubscription));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(pendingSubscription);
        when(subscriptionMapper.toResponse(any(Subscription.class))).thenReturn(
                SubscriptionResponse.builder()
                        .subscriptionId(subscriptionId)
                        .status(SubscriptionStatus.ACTIVE)
                        .build()
        );

        // When
        SubscriptionResponse result = subscriptionService.activateSubscription(subscriptionId);

        // Then
        assertNotNull(result);
        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(captor.capture());
        assertEquals(SubscriptionStatus.ACTIVE, captor.getValue().getStatus());
    }
}

