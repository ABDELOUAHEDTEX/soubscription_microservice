package com.transport.subscription.exception;

/**
 * Exception levée lorsqu'un abonnement est expiré
 */
public class SubscriptionExpiredException extends RuntimeException {
    
    public SubscriptionExpiredException(String message) {
        super(message);
    }
    
    public SubscriptionExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}

