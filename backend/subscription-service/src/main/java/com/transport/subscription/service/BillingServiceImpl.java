package com.transport.subscription.service;

import com.transport.subscription.dto.mapper.PaymentMapper;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.exception.SubscriptionNotFoundException;
import com.transport.subscription.model.PaymentMethod;
import com.transport.subscription.model.PaymentStatus;
import com.transport.subscription.model.PaymentType;
import com.transport.subscription.model.Subscription;
import com.transport.subscription.model.SubscriptionPayment;
import com.transport.subscription.repository.BillingHistoryRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service pour la gestion de la facturation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BillingServiceImpl implements BillingService {

    private final BillingHistoryRepository billingHistoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getBillingHistory(UUID subscriptionId) {
        log.debug("Récupération de l'historique de facturation pour l'abonnement: {}", subscriptionId);
        
        // Vérifier que l'abonnement existe
        subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> {
                    log.error("Abonnement non trouvé pour l'historique de facturation: {}", subscriptionId);
                    return new SubscriptionNotFoundException(
                            "Subscription not found with id: " + subscriptionId);
                });

        List<SubscriptionPayment> payments = billingHistoryRepository
                .findBySubscriptionIdOrderByPaymentDateDesc(subscriptionId);
        log.info("{} paiement(s) trouvé(s) pour l'abonnement: {}", payments.size(), subscriptionId);
        return paymentMapper.toResponseList(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID paymentId) {
        log.debug("Récupération du paiement avec ID: {}", paymentId);
        SubscriptionPayment payment = billingHistoryRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Paiement non trouvé avec l'ID: {}", paymentId);
                    return new SubscriptionNotFoundException(
                            "Payment not found with id: " + paymentId);
                });
        log.debug("Paiement trouvé: {} - Statut: {}", paymentId, payment.getPaymentStatus());
        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse recordSuccessfulPayment(
            UUID subscriptionId,
            BigDecimal amount,
            String currency,
            String externalTxnId,
            String idempotencyKey,
            PaymentMethod paymentMethod) {
        
        log.info("Enregistrement d'un paiement réussi pour l'abonnement: {}", subscriptionId);

        // Vérifier l'idempotence
        if (idempotencyKey != null && billingHistoryRepository.existsByIdempotencyKey(idempotencyKey)) {
            log.warn("Paiement déjà enregistré avec la clé d'idempotence: {}", idempotencyKey);
            SubscriptionPayment existingPayment = billingHistoryRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseThrow();
            return paymentMapper.toResponse(existingPayment);
        }

        // Récupérer l'abonnement
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found with id: " + subscriptionId));

        // Créer le paiement
        SubscriptionPayment payment = SubscriptionPayment.builder()
                .subscription(subscription)
                .amount(amount)
                .currency(currency)
                .paymentStatus(PaymentStatus.SUCCEEDED)
                .paymentMethod(paymentMethod)
                .paymentType(PaymentType.INITIAL) // Peut être déterminé selon le contexte
                .externalTxnId(externalTxnId)
                .idempotencyKey(idempotencyKey)
                .build();

        payment = billingHistoryRepository.save(payment);
        log.debug("Paiement créé avec ID: {}, Montant: {} {}", 
                payment.getPaymentId(), amount, currency);

        // Mettre à jour le montant payé de l'abonnement
        BigDecimal newAmountPaid = subscription.getAmountPaid().add(amount);
        subscription.setAmountPaid(newAmountPaid);
        subscriptionRepository.save(subscription);
        log.debug("Montant total payé mis à jour pour l'abonnement {}: {}", 
                subscriptionId, newAmountPaid);

        log.info("Paiement enregistré avec succès: {} pour l'abonnement: {}", 
                payment.getPaymentId(), subscriptionId);
        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse recordFailedPayment(
            UUID subscriptionId,
            BigDecimal amount,
            String currency,
            String failureReason,
            String idempotencyKey,
            PaymentMethod paymentMethod) {
        
        log.warn("Enregistrement d'un paiement échoué pour l'abonnement: {}. Raison: {}", 
                subscriptionId, failureReason);

        // Récupérer l'abonnement
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> {
                    log.error("Abonnement non trouvé pour l'enregistrement du paiement échoué: {}", subscriptionId);
                    return new SubscriptionNotFoundException(
                            "Subscription not found with id: " + subscriptionId);
                });

        // Créer le paiement échoué
        SubscriptionPayment payment = SubscriptionPayment.builder()
                .subscription(subscription)
                .amount(amount)
                .currency(currency)
                .paymentStatus(PaymentStatus.FAILED)
                .paymentMethod(paymentMethod)
                .paymentType(PaymentType.INITIAL)
                .failureReason(failureReason)
                .idempotencyKey(idempotencyKey)
                .build();

        payment = billingHistoryRepository.save(payment);
        log.warn("Paiement échoué enregistré: {} pour l'abonnement: {}. Raison: {}", 
                payment.getPaymentId(), subscriptionId, failureReason);
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidAmount(UUID subscriptionId) {
        log.debug("Calcul du montant total payé pour l'abonnement: {}", subscriptionId);
        
        Double total = billingHistoryRepository.calculateTotalPaidAmount(
                subscriptionId, PaymentStatus.SUCCEEDED);
        BigDecimal totalAmount = BigDecimal.valueOf(total != null ? total : 0.0);
        log.info("Montant total payé pour l'abonnement {}: {}", subscriptionId, totalAmount);
        return totalAmount;
    }
}

