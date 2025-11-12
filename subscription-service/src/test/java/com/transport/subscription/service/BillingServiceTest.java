package com.transport.subscription.service;

import com.transport.subscription.dto.mapper.PaymentMapper;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.exception.SubscriptionNotFoundException;
import com.transport.subscription.model.PaymentMethod;
import com.transport.subscription.model.PaymentStatus;
import com.transport.subscription.model.Subscription;
import com.transport.subscription.model.SubscriptionPayment;
import com.transport.subscription.repository.BillingHistoryRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour BillingService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BillingService Tests")
class BillingServiceTest {

    @Mock
    private BillingHistoryRepository billingHistoryRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private BillingServiceImpl billingService;

    private UUID subscriptionId;
    private UUID paymentId;
    private Subscription testSubscription;
    private SubscriptionPayment testPayment;

    @BeforeEach
    void setUp() {
        subscriptionId = UUID.randomUUID();
        paymentId = UUID.randomUUID();

        testSubscription = Subscription.builder()
                .subscriptionId(subscriptionId)
                .amountPaid(BigDecimal.ZERO)
                .build();

        testPayment = SubscriptionPayment.builder()
                .paymentId(paymentId)
                .subscription(testSubscription)
                .amount(new BigDecimal("29.99"))
                .currency("EUR")
                .paymentStatus(PaymentStatus.SUCCEEDED)
                .paymentMethod(PaymentMethod.CARD)
                .build();
    }

    @Test
    @DisplayName("Devrait retourner l'historique de facturation")
    void shouldReturnBillingHistory() {
        // Given
        List<SubscriptionPayment> payments = Arrays.asList(testPayment);
        List<PaymentResponse> expectedResponses = Arrays.asList(
                PaymentResponse.builder()
                        .paymentId(paymentId)
                        .amount(new BigDecimal("29.99"))
                        .build()
        );

        when(subscriptionRepository.findById(subscriptionId))
                .thenReturn(Optional.of(testSubscription));
        when(billingHistoryRepository.findBySubscriptionIdOrderByPaymentDateDesc(subscriptionId))
                .thenReturn(payments);
        when(paymentMapper.toResponseList(payments)).thenReturn(expectedResponses);

        // When
        List<PaymentResponse> result = billingService.getBillingHistory(subscriptionId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(subscriptionRepository).findById(subscriptionId);
        verify(billingHistoryRepository).findBySubscriptionIdOrderByPaymentDateDesc(subscriptionId);
    }

    @Test
    @DisplayName("Devrait lever une exception si l'abonnement n'existe pas")
    void shouldThrowExceptionWhenSubscriptionNotFound() {
        // Given
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubscriptionNotFoundException.class, () -> billingService.getBillingHistory(subscriptionId));
    }

    @Test
    @DisplayName("Devrait enregistrer un paiement réussi")
    void shouldRecordSuccessfulPayment() {
        // Given
        BigDecimal amount = new BigDecimal("29.99");
        String currency = "EUR";
        String externalTxnId = "ext-123";
        String idempotencyKey = "idemp-123";

        when(subscriptionRepository.findById(subscriptionId))
                .thenReturn(Optional.of(testSubscription));
        when(billingHistoryRepository.existsByIdempotencyKey(idempotencyKey))
                .thenReturn(false);
        when(billingHistoryRepository.save(any(SubscriptionPayment.class)))
                .thenReturn(testPayment);
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(testSubscription);
        when(paymentMapper.toResponse(testPayment)).thenReturn(
                PaymentResponse.builder()
                        .paymentId(paymentId)
                        .amount(amount)
                        .paymentStatus(PaymentStatus.SUCCEEDED)
                        .build()
        );

        // When
        PaymentResponse result = billingService.recordSuccessfulPayment(
                subscriptionId, amount, currency, externalTxnId, idempotencyKey, testPayment.getPaymentMethod());

        // Then
        assertNotNull(result);
        assertEquals(paymentId, result.getPaymentId());
        assertEquals(PaymentStatus.SUCCEEDED, result.getPaymentStatus());
        verify(billingHistoryRepository).save(any(SubscriptionPayment.class));
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("Devrait retourner le paiement existant si idempotence key existe")
    void shouldReturnExistingPaymentIfIdempotencyKeyExists() {
        // Given
        BigDecimal amount = new BigDecimal("29.99");
        String idempotencyKey = "idemp-123";

        when(billingHistoryRepository.existsByIdempotencyKey(idempotencyKey))
                .thenReturn(true);
        when(billingHistoryRepository.findByIdempotencyKey(idempotencyKey))
                .thenReturn(Optional.of(testPayment));
        when(paymentMapper.toResponse(testPayment)).thenReturn(
                PaymentResponse.builder()
                        .paymentId(paymentId)
                        .build()
        );

        // When
        PaymentResponse result = billingService.recordSuccessfulPayment(
                subscriptionId, amount, "EUR", "ext-123", idempotencyKey, testPayment.getPaymentMethod());

        // Then
        assertNotNull(result);
        verify(billingHistoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Devrait calculer le montant total payé")
    void shouldCalculateTotalPaidAmount() {
        // Given
        Double total = 59.98;
        when(billingHistoryRepository.calculateTotalPaidAmount(
                subscriptionId, PaymentStatus.SUCCEEDED)).thenReturn(total);

        // When
        BigDecimal result = billingService.getTotalPaidAmount(subscriptionId);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("59.98"), result);
        verify(billingHistoryRepository).calculateTotalPaidAmount(
                subscriptionId, PaymentStatus.SUCCEEDED);
    }
}

