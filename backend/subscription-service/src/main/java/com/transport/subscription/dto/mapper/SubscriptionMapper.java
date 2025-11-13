package com.transport.subscription.dto.mapper;

import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.model.Plan;
import com.transport.subscription.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper MapStruct pour convertir entre Subscription (Entity) et les DTOs
 * MapStruct génère automatiquement l'implémentation à la compilation
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SubscriptionMapper {

    /**
     * Convertit une entité Subscription en SubscriptionResponse
     * Mappe automatiquement les champs avec les mêmes noms
     * 
     * @param subscription Entité Subscription
     * @return SubscriptionResponse
     */
    @Mapping(source = "subscriptionId", target = "subscriptionId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "plan.planId", target = "planId")
    @Mapping(source = "plan.planCode", target = "planCode")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "nextBillingDate", target = "nextBillingDate")
    @Mapping(source = "amountPaid", target = "amountPaid")
    @Mapping(source = "autoRenewEnabled", target = "autoRenewEnabled")
    @Mapping(source = "cardExpMonth", target = "cardExpMonth")
    @Mapping(source = "cardExpYear", target = "cardExpYear")
    @Mapping(source = "qrCodeData", target = "qrCodeData")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(target = "cardLastFour", expression = "java(extractCardLastFour(subscription.getCardToken()))")
    SubscriptionResponse toResponse(Subscription subscription);

    /**
     * Convertit une liste de Subscriptions en liste de SubscriptionResponses
     * @param subscriptions Liste d'entités Subscription
     * @return Liste de SubscriptionResponses
     */
    List<SubscriptionResponse> toResponseList(List<Subscription> subscriptions);

    /**
     * Crée une entité Subscription à partir d'un CreateSubscriptionRequest
     * Note: Les champs comme subscriptionId, createdAt, etc. seront générés automatiquement
     * 
     * @param request CreateSubscriptionRequest
     * @param plan Plan associé
     * @return Subscription (à compléter avec les autres champs)
     */
    @Mapping(target = "subscriptionId", ignore = true)
    @Mapping(target = "plan", source = "plan")
    @Mapping(target = "userId", source = "request.userId")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "startDate", ignore = true) // Sera calculé dans le service
    @Mapping(target = "endDate", ignore = true) // Sera calculé dans le service
    @Mapping(target = "nextBillingDate", ignore = true) // Sera calculé dans le service
    @Mapping(target = "amountPaid", constant = "0")
    @Mapping(target = "autoRenewEnabled", source = "request.autoRenewEnabled", 
             defaultExpression = "java(true)")
    @Mapping(target = "cardToken", source = "request.cardToken")
    @Mapping(target = "cardExpMonth", source = "request.cardExpMonth")
    @Mapping(target = "cardExpYear", source = "request.cardExpYear")
    @Mapping(target = "qrCodeData", ignore = true) // Sera généré dans le service
    @Mapping(target = "createdAt", ignore = true) // Sera généré par @PrePersist
    @Mapping(target = "updatedAt", ignore = true) // Sera généré par @PrePersist
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "history", ignore = true)
    Subscription toEntity(CreateSubscriptionRequest request, Plan plan);

    /**
     * Met à jour une entité Subscription avec les données d'un UpdateSubscriptionRequest
     * 
     * @param request UpdateSubscriptionRequest
     * @param subscription Entité Subscription à mettre à jour
     */
    @Mapping(target = "subscriptionId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "nextBillingDate", ignore = true)
    @Mapping(target = "amountPaid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true) // Sera mis à jour par @PreUpdate
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "history", ignore = true)
    void updateFromRequest(UpdateSubscriptionRequest request, @MappingTarget Subscription subscription);

    /**
     * Méthode helper pour extraire les 4 derniers chiffres d'un token de carte
     * Cette méthode sera implémentée manuellement dans l'implémentation générée
     */
    default String extractCardLastFour(String cardToken) {
        if (cardToken == null || cardToken.length() < 4) {
            return null;
        }
        return cardToken.substring(cardToken.length() - 4);
    }
}

