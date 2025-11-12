package com.transport.subscription.service;

import com.transport.subscription.dto.mapper.PlanMapper;
import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.exception.PlanNotFoundException;
import com.transport.subscription.model.Plan;
import com.transport.subscription.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour PlanService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PlanService Tests")
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PlanMapper planMapper;

    @InjectMocks
    private PlanServiceImpl planService;

    private Plan testPlan;
    private PlanResponse testPlanResponse;
    private UUID planId;

    @BeforeEach
    void setUp() {
        planId = UUID.randomUUID();
        
        testPlan = Plan.builder()
                .planId(planId)
                .planCode("MONTHLY_BASIC")
                .description("Plan mensuel de base")
                .durationDays(30)
                .price(new BigDecimal("29.99"))
                .currency("EUR")
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        testPlanResponse = PlanResponse.builder()
                .planId(planId)
                .planCode("MONTHLY_BASIC")
                .description("Plan mensuel de base")
                .durationDays(30)
                .price(new BigDecimal("29.99"))
                .currency("EUR")
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Devrait retourner tous les plans actifs")
    void shouldReturnAllActivePlans() {
        // Given
        List<Plan> plans = Arrays.asList(testPlan);
        List<PlanResponse> expectedResponses = Arrays.asList(testPlanResponse);
        
        when(planRepository.findByIsActive(true)).thenReturn(plans);
        when(planMapper.toResponseList(plans)).thenReturn(expectedResponses);

        // When
        List<PlanResponse> result = planService.getAllActivePlans();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("MONTHLY_BASIC", result.get(0).getPlanCode());
        verify(planRepository).findByIsActive(true);
        verify(planMapper).toResponseList(plans);
    }

    @Test
    @DisplayName("Devrait retourner un plan par ID")
    void shouldReturnPlanById() {
        // Given
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(planMapper.toResponse(testPlan)).thenReturn(testPlanResponse);

        // When
        PlanResponse result = planService.getPlanById(planId);

        // Then
        assertNotNull(result);
        assertEquals(planId, result.getPlanId());
        assertEquals("MONTHLY_BASIC", result.getPlanCode());
        verify(planRepository).findById(planId);
        verify(planMapper).toResponse(testPlan);
    }

    @Test
    @DisplayName("Devrait lever une exception si le plan n'existe pas")
    void shouldThrowExceptionWhenPlanNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(planRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PlanNotFoundException.class, () -> {
            planService.getPlanById(nonExistentId);
        });
        verify(planRepository).findById(nonExistentId);
        verify(planMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Devrait retourner un plan par code")
    void shouldReturnPlanByCode() {
        // Given
        String planCode = "MONTHLY_BASIC";
        when(planRepository.findByPlanCode(planCode)).thenReturn(Optional.of(testPlan));
        when(planMapper.toResponse(testPlan)).thenReturn(testPlanResponse);

        // When
        PlanResponse result = planService.getPlanByCode(planCode);

        // Then
        assertNotNull(result);
        assertEquals(planCode, result.getPlanCode());
        verify(planRepository).findByPlanCode(planCode);
        verify(planMapper).toResponse(testPlan);
    }

    @Test
    @DisplayName("Devrait retourner tous les plans")
    void shouldReturnAllPlans() {
        // Given
        Plan inactivePlan = Plan.builder()
                .planId(UUID.randomUUID())
                .planCode("OLD_PLAN")
                .isActive(false)
                .build();
        
        List<Plan> allPlans = Arrays.asList(testPlan, inactivePlan);
        List<PlanResponse> expectedResponses = Arrays.asList(testPlanResponse);
        
        when(planRepository.findAll()).thenReturn(allPlans);
        when(planMapper.toResponseList(allPlans)).thenReturn(expectedResponses);

        // When
        List<PlanResponse> result = planService.getAllPlans();

        // Then
        assertNotNull(result);
        verify(planRepository).findAll();
        verify(planMapper).toResponseList(allPlans);
    }
}

