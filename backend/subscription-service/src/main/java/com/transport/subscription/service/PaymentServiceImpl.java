package com.transport.subscription.service;

import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.exception.InvalidSubscriptionException;
import com.transport.subscription.exception.PaymentFailedException;
import com.transport.subscription.exception.SubscriptionNotFoundException;
import com.transport.subscription.model.PaymentMethod;
import com.transport.subscription.model.Subscription;
import com.transport.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service de paiement qui orchestre la passerelle et la facturation interne.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentGateway paymentGateway;
    private final BillingService billingService;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        log.info("Traitement d'un paiement pour l'abonnement: {}", request.getSubscriptionId());

        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found with id: " + request.getSubscriptionId()));

        if (request.getPaymentMethod() == PaymentMethod.CARD
                && (request.getCardToken() == null || request.getCardToken().isBlank())) {
            throw new InvalidSubscriptionException("Card token is required for card payments");
        }

        PaymentGateway.PaymentRequest gatewayRequest = new PaymentGateway.PaymentRequest(
                request.getSubscriptionId(),
                subscription.getUserId(),
                request.getAmount(),
                request.getCurrency(),
                request.getPaymentMethod(),
                request.getCardToken(),
                request.getIdempotencyKey()
        );

        PaymentGateway.PaymentResult gatewayResult = paymentGateway.charge(gatewayRequest);

        if (gatewayResult.success()) {
            log.info("Paiement accepté par la passerelle. Transaction externe: {}",
                    gatewayResult.externalTransactionId());
            return billingService.recordSuccessfulPayment(
                    subscription.getSubscriptionId(),
                    request.getAmount(),
                    request.getCurrency(),
                    gatewayResult.externalTransactionId(),
                    request.getIdempotencyKey(),
                    request.getPaymentMethod()
            );
        }

        String failureReason = gatewayResult.failureReason() != null
                ? gatewayResult.failureReason()
                : "Payment declined by gateway";
        billingService.recordFailedPayment(
                subscription.getSubscriptionId(),
                request.getAmount(),
                request.getCurrency(),
                failureReason,
                request.getIdempotencyKey(),
                request.getPaymentMethod()
        );

        throw new PaymentFailedException(failureReason);
    }

    @Override
    public PaymentResponse refundPayment(UUID paymentId) {
        log.warn("Demande de remboursement non implémentée pour le paiement: {}", paymentId);
        // Vérifie que le paiement existe afin de retourner une erreur claire si nécessaire
        billingService.getPaymentById(paymentId);
        throw new UnsupportedOperationException("Refund processing is not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsBySubscriptionId(UUID subscriptionId) {
        return billingService.getBillingHistory(subscriptionId);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID paymentId) {
        return billingService.getPaymentById(paymentId);
    }
}
