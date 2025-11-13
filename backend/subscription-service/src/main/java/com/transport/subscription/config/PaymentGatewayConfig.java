package com.transport.subscription.config;

import com.transport.subscription.model.PaymentMethod;
import com.transport.subscription.service.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Configuration de la passerelle de paiement.
 * Fournit une implémentation par défaut (simulée) qui peut être substituée par une intégration réelle.
 */
@Configuration
@Slf4j
public class PaymentGatewayConfig {

    @Value("${payment.gateway.provider:mock}")
    private String provider;

    @Bean
    public PaymentGateway paymentGateway() {
        // Pour l'instant, nous retournons une implémentation simulée.
        // Cette implémentation pourra être remplacée par StripePaymentGateway ou PayPalPaymentGateway.
        log.info("Initialisation de la passerelle de paiement '{}'", provider);
        return new MockPaymentGateway();
    }

    /**
     * Implémentation simulée d'une passerelle de paiement.
     */
    static class MockPaymentGateway implements PaymentGateway {

        @Override
        public PaymentResult charge(PaymentRequest request) {
            // Simulation d'un paiement : les paiements par carte avec token sont acceptés, sinon échec.
            boolean success = request.paymentMethod() == PaymentMethod.CARD
                    ? request.cardToken() != null && !request.cardToken().isBlank()
                    : true;

            if (success) {
                String externalTxnId = "mock-" + UUID.randomUUID();
                return new PaymentResult(true, externalTxnId, null);
            }

            return new PaymentResult(false, null, "Payment declined by mock gateway");
        }

        @Override
        public RefundResult refund(String externalTransactionId, BigDecimal amount) {
            if (externalTransactionId == null || externalTransactionId.isBlank()) {
                return new RefundResult(false, null, "Missing external transaction id");
            }
            // Génère un identifiant de remboursement simulé
            String refundTxnId = externalTransactionId + "-refund-" + Instant.now().toEpochMilli();
            return new RefundResult(true, refundTxnId, null);
        }

        @Override
        public boolean verifyWebhookSignature(String payload, String signature) {
            // Implémentation simulée : retourne toujours vrai
            return true;
        }
    }
}
