package com.transport.subscription.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour mettre à jour un abonnement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionRequest {

    private Boolean autoRenewEnabled;

    // Informations de carte mises à jour
    private String cardToken;
    
    @Min(value = 1, message = "Card expiration month must be between 1 and 12")
    @Max(value = 12, message = "Card expiration month must be between 1 and 12")
    private Integer cardExpMonth;
    
    private Integer cardExpYear;
}

