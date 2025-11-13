package com.transport.subscription.dto.mapper;

import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.model.SubscriptionPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct pour convertir entre SubscriptionPayment (Entity) et PaymentResponse (DTO)
 * MapStruct génère automatiquement l'implémentation à la compilation
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    /**
     * Convertit une entité SubscriptionPayment en PaymentResponse
     * @param payment Entité SubscriptionPayment
     * @return PaymentResponse
     */
    @Mapping(source = "subscription.subscriptionId", target = "subscriptionId")
    PaymentResponse toResponse(SubscriptionPayment payment);

    /**
     * Convertit une liste de SubscriptionPayments en liste de PaymentResponses
     * @param payments Liste d'entités SubscriptionPayment
     * @return Liste de PaymentResponses
     */
    List<PaymentResponse> toResponseList(List<SubscriptionPayment> payments);
}
