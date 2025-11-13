package com.transport.subscription.controller;

import com.transport.subscription.dto.request.CancelSubscriptionRequest;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.RenewSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.service.SubscriptionService;
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

import java.util.List;
import java.util.UUID;

/**
 * Controller REST pour la gestion des abonnements
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subscriptions", description = "API pour la gestion des abonnements utilisateurs")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(
            summary = "Créer un nouvel abonnement",
            description = "Crée un nouvel abonnement pour un utilisateur avec un plan spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Abonnement créé avec succès",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requête invalide (plan non trouvé ou abonnement actif existant)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plan non trouvé"
            )
    })
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request) {
        log.info("Requête POST /api/subscriptions - Création d'un abonnement pour l'utilisateur: {}", 
                request.getUserId());
        SubscriptionResponse subscription = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @GetMapping("/{subscriptionId}")
    @Operation(
            summary = "Récupérer un abonnement par ID",
            description = "Retourne les détails d'un abonnement spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Abonnement trouvé",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Abonnement non trouvé"
            )
    })
    public ResponseEntity<SubscriptionResponse> getSubscriptionById(
            @Parameter(description = "ID de l'abonnement", required = true)
            @PathVariable UUID subscriptionId) {
        log.info("Requête GET /api/subscriptions/{} - Récupération de l'abonnement", subscriptionId);
        SubscriptionResponse subscription = subscriptionService.getSubscriptionById(subscriptionId);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/users/{userId}")
    @Operation(
            summary = "Récupérer les abonnements d'un utilisateur",
            description = "Retourne la liste de tous les abonnements d'un utilisateur"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des abonnements récupérée avec succès"
            )
    })
    public ResponseEntity<List<SubscriptionResponse>> getUserSubscriptions(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable UUID userId) {
        log.info("Requête GET /api/subscriptions/users/{} - Récupération des abonnements", userId);
        List<SubscriptionResponse> subscriptions = subscriptionService.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/users/{userId}/active")
    @Operation(
            summary = "Récupérer les abonnements actifs d'un utilisateur",
            description = "Retourne la liste des abonnements actifs d'un utilisateur"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des abonnements actifs récupérée avec succès"
            )
    })
    public ResponseEntity<List<SubscriptionResponse>> getActiveUserSubscriptions(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable UUID userId) {
        log.info("Requête GET /api/subscriptions/users/{}/active - Récupération des abonnements actifs", userId);
        List<SubscriptionResponse> subscriptions = subscriptionService.getActiveUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @PutMapping("/{subscriptionId}")
    @Operation(
            summary = "Mettre à jour un abonnement",
            description = "Met à jour les informations d'un abonnement (carte, renouvellement automatique, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Abonnement mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Abonnement non trouvé"
            )
    })
    public ResponseEntity<SubscriptionResponse> updateSubscription(
            @Parameter(description = "ID de l'abonnement", required = true)
            @PathVariable UUID subscriptionId,
            @Valid @RequestBody UpdateSubscriptionRequest request) {
        log.info("Requête PUT /api/subscriptions/{} - Mise à jour de l'abonnement", subscriptionId);
        SubscriptionResponse subscription = subscriptionService.updateSubscription(subscriptionId, request);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/{subscriptionId}/cancel")
    @Operation(
            summary = "Annuler un abonnement",
            description = "Annule un abonnement (immédiatement ou à la fin de la période)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Abonnement annulé avec succès",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Impossible d'annuler (abonnement déjà expiré)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Abonnement non trouvé"
            )
    })
    public ResponseEntity<SubscriptionResponse> cancelSubscription(
            @Parameter(description = "ID de l'abonnement", required = true)
            @PathVariable UUID subscriptionId,
            @RequestBody(required = false) CancelSubscriptionRequest request) {
        log.info("Requête POST /api/subscriptions/{}/cancel - Annulation de l'abonnement", subscriptionId);
        
        // Si aucune requête n'est fournie, créer une requête par défaut
        if (request == null) {
            request = CancelSubscriptionRequest.builder()
                    .immediate(false)
                    .build();
        }
        
        SubscriptionResponse subscription = subscriptionService.cancelSubscription(subscriptionId, request);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/{subscriptionId}/renew")
    @Operation(
            summary = "Renouveler un abonnement",
            description = "Renouvelle un abonnement (avec option de changement de plan)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Abonnement renouvelé avec succès",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Impossible de renouveler (abonnement expiré)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Abonnement ou plan non trouvé"
            )
    })
    public ResponseEntity<SubscriptionResponse> renewSubscription(
            @Parameter(description = "ID de l'abonnement", required = true)
            @PathVariable UUID subscriptionId,
            @RequestBody(required = false) RenewSubscriptionRequest request) {
        log.info("Requête POST /api/subscriptions/{}/renew - Renouvellement de l'abonnement", subscriptionId);
        
        // Si aucune requête n'est fournie, créer une requête par défaut
        if (request == null) {
            request = RenewSubscriptionRequest.builder().build();
        }
        
        SubscriptionResponse subscription = subscriptionService.renewSubscription(subscriptionId, request);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/{subscriptionId}/activate")
    @Operation(
            summary = "Activer un abonnement",
            description = "Active un abonnement après un paiement réussi (appelé par le service de paiement)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Abonnement activé avec succès",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Impossible d'activer (statut invalide)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Abonnement non trouvé"
            )
    })
    public ResponseEntity<SubscriptionResponse> activateSubscription(
            @Parameter(description = "ID de l'abonnement", required = true)
            @PathVariable UUID subscriptionId) {
        log.info("Requête POST /api/subscriptions/{}/activate - Activation de l'abonnement", subscriptionId);
        SubscriptionResponse subscription = subscriptionService.activateSubscription(subscriptionId);
        return ResponseEntity.ok(subscription);
    }
}

