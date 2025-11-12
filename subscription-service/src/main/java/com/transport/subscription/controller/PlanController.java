package com.transport.subscription.controller;

import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST pour la gestion des plans d'abonnement
 */
@RestController
@RequestMapping("/api/subscriptions/plans")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Plans", description = "API pour la gestion des plans d'abonnement")
public class PlanController {

    private final PlanService planService;

    @GetMapping
    @Operation(
            summary = "Récupérer tous les plans actifs",
            description = "Retourne la liste de tous les plans d'abonnement actifs"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des plans actifs récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = PlanResponse.class))
            )
    })
    public ResponseEntity<List<PlanResponse>> getAllActivePlans() {
        log.info("Requête GET /api/subscriptions/plans - Récupération de tous les plans actifs");
        List<PlanResponse> plans = planService.getAllActivePlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Récupérer tous les plans",
            description = "Retourne la liste de tous les plans d'abonnement (actifs et inactifs)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste de tous les plans récupérée avec succès"
            )
    })
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        log.info("Requête GET /api/subscriptions/plans/all - Récupération de tous les plans");
        List<PlanResponse> plans = planService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}")
    @Operation(
            summary = "Récupérer un plan par ID",
            description = "Retourne les détails d'un plan d'abonnement spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plan trouvé",
                    content = @Content(schema = @Schema(implementation = PlanResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plan non trouvé"
            )
    })
    public ResponseEntity<PlanResponse> getPlanById(
            @Parameter(description = "ID du plan", required = true)
            @PathVariable UUID planId) {
        log.info("Requête GET /api/subscriptions/plans/{} - Récupération du plan", planId);
        PlanResponse plan = planService.getPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/code/{planCode}")
    @Operation(
            summary = "Récupérer un plan par code",
            description = "Retourne les détails d'un plan d'abonnement par son code unique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plan trouvé",
                    content = @Content(schema = @Schema(implementation = PlanResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plan non trouvé"
            )
    })
    public ResponseEntity<PlanResponse> getPlanByCode(
            @Parameter(description = "Code du plan", required = true)
            @PathVariable String planCode) {
        log.info("Requête GET /api/subscriptions/plans/code/{} - Récupération du plan par code", planCode);
        PlanResponse plan = planService.getPlanByCode(planCode);
        return ResponseEntity.ok(plan);
    }
}

