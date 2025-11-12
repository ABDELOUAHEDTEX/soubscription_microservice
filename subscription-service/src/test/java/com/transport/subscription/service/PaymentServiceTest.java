package com.transport.subscription.service;

import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.exception.InvalidSubscriptionException;
import com.transport.subscription.exception.PaymentFailedException;
import com.transport.subscription.exception.SubscriptionNotFoundException;
import com.transport.subscription.model.PaymentMethod;
import com.transport.subscription.model.Subscription;
import com.transport.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour PaymentService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Tests")
class PaymentServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private BillingService billingService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private UUID subscriptionId;
    private UUID userId;
    private Subscription subscription;
    private ProcessPaymentRequest request;

    @BeforeEach
    void setUp() {
        subscriptionId = UUID.randomUUID();
        userId = UUID.randomUUID();

        subscription = Subscription.builder()
                .subscriptionId(subscriptionId)
                .userId(userId)
                .build();

        request = ProcessPaymentRequest.builder()
                .subscriptionId(subscriptionId)
                .amount(new BigDecimal("19.99"))
                .currency("EUR")
                .paymentMethod(PaymentMethod.CARD)
                .cardToken("tok_test")
                .idempotencyKey("idemp-123")
                .build();
    }

    @Test
    @DisplayName("Devrait traiter un paiement avec succès")
    void shouldProcessPaymentSuccessfully() {
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(paymentGateway.charge(any())).thenReturn(new PaymentGateway.PaymentResult(true, "ext-1", null));

        PaymentResponse expected = PaymentResponse.builder()
                .paymentId(UUID.randomUUID())
                .build();
        when(billingService.recordSuccessfulPayment(any(), any(), any(), any(), any(), any()))
                .thenReturn(expected);

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        verify(billingService).recordSuccessfulPayment(
                eq(subscriptionId),
                eq(request.getAmount()),
                eq(request.getCurrency()),
                eq("ext-1"),
                eq(request.getIdempotencyKey()),
                eq(request.getPaymentMethod())
        );
    }

    @Test
    @DisplayName("Devrait lever une exception si l'abonnement est introuvable")
    void shouldThrowWhenSubscriptionNotFound() {
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () -> paymentService.processPayment(request));
    }

    @Test
    @DisplayName("Devrait lever une exception si token carte manquant")
    void shouldThrowWhenCardTokenMissing() {
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        ProcessPaymentRequest invalidRequest = request.toBuilder().cardToken(null).build();

        assertThrows(InvalidSubscriptionException.class, () -> paymentService.processPayment(invalidRequest));
    }

    @Test
    @DisplayName("Devrait enregistrer un paiement échoué")
    void shouldRecordFailedPaymentWhenGatewayFails() {
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(paymentGateway.charge(any())).thenReturn(new PaymentGateway.PaymentResult(false, null, "Declined"));

        assertThrows(PaymentFailedException.class, () -> paymentService.processPayment(request));
        verify(billingService).recordFailedPayment(
                eq(subscriptionId),
                eq(request.getAmount()),
                eq(request.getCurrency()),
                eq("Declined"),
                eq(request.getIdempotencyKey()),
                eq(request.getPaymentMethod())
        );
    }
}
