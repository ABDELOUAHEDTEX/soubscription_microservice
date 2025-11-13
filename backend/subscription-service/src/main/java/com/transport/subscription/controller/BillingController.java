package com.transport.subscription.controller;

import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.service.BillingService;
import com.transport.subscription.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST pour la gestion de la facturation et des paiements
 */
@RestController
@RequestMapping("/api/subscriptions/billing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Billing", description = "API pour la gestion de la facturation et des paiements")
public class BillingController {

    private final BillingService billingService;
    private final PaymentService paymentService;

    @PostMapping("/payments")
    @Operation(
            summary = "Traiter un paiement",
            description = "Traite un paiement pour un abonnement donné"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Paiement traité avec succès",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "402",
                    description = "Paiement refusé"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Abonnement non trouvé"
            )
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request) {
        log.info("Requête POST /api/subscriptions/billing/payments - Traitement d'un paiement");
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/subscriptions/{subscriptionId}")
    @Operation(
            summary = "Récupérer l'historique de facturation d'un abonnement",
            description = "Retourne la liste de tous les paiements associés à un abonnement"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historique de facturation récupéré avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Abonnement non trouvé"
            )
    })
    public ResponseEntity<List<PaymentResponse>> getBillingHistory(
            @Parameter(description = "ID de l'abonnement", required = true)
            @PathVariable UUID subscriptionId) {
        log.info("Requête GET /api/subscriptions/billing/subscriptions/{} - Récupération de l'historique", 
                subscriptionId);
        List<PaymentResponse> history = billingService.getBillingHistory(subscriptionId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/payments/{paymentId}")
    @Operation(
            summary = "Récupérer un paiement par ID",
            description = "Retourne les détails d'un paiement spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paiement trouvé",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Paiement non trouvé"
            )
    })
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(description = "ID du paiement", required = true)
            @PathVariable UUID paymentId) {
        log.info("Requête GET /api/subscriptions/billing/payments/{} - Récupération du paiement", paymentId);
        PaymentResponse payment = billingService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/subscriptions/{subscriptionId}/total")
    @Operation(
            summary = "Calculer le montant total payé",
            description = "Calcule le montant total des paiements réussis pour un abonnement"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Montant total calculé avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Abonnement non trouvé"
            )
    })
    public ResponseEntity<BigDecimal> getTotalPaidAmount(
            @Parameter(description = "ID de l'abonnement", required = true)
            @PathVariable UUID subscriptionId) {
        log.info("Requête GET /api/subscriptions/billing/subscriptions/{}/total - Calcul du montant total", 
                subscriptionId);
        BigDecimal total = billingService.getTotalPaidAmount(subscriptionId);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/payments/{paymentId}/refund")
    @Operation(
            summary = "Demander un remboursement",
            description = "Demande un remboursement pour un paiement spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Remboursement traité",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "501",
                    description = "Fonctionnalité non implémentée"
            )
    })
    public ResponseEntity<PaymentResponse> refundPayment(
            @Parameter(description = "ID du paiement", required = true)
            @PathVariable UUID paymentId) {
        log.info("Requête POST /api/subscriptions/billing/payments/{}/refund - Remboursement", paymentId);
        PaymentResponse response = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(response);
    }
}

