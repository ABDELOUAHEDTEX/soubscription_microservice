package com.transport.subscription.dto.mapper;

import com.transport.subscription.dto.response.SubscriptionHistoryResponse;
import com.transport.subscription.model.SubscriptionHistory;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper MapStruct pour convertir entre SubscriptionHistory (Entity) et SubscriptionHistoryResponse (DTO)
 * MapStruct génère automatiquement l'implémentation à la compilation
 */
@Mapper(componentModel = "spring")
public interface SubscriptionHistoryMapper {

    /**
     * Convertit une entité SubscriptionHistory en SubscriptionHistoryResponse
     * @param history Entité SubscriptionHistory
     * @return SubscriptionHistoryResponse
     */
    @org.mapstruct.Mapping(source = "subscription.subscriptionId", target = "subscriptionId")
    SubscriptionHistoryResponse toResponse(SubscriptionHistory history);

    /**
     * Convertit une liste de SubscriptionHistories en liste de SubscriptionHistoryResponses
     * @param histories Liste d'entités SubscriptionHistory
     * @return Liste de SubscriptionHistoryResponses
     */
    List<SubscriptionHistoryResponse> toResponseList(List<SubscriptionHistory> histories);
}

