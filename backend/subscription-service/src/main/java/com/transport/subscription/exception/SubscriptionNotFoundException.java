package com.transport.subscription.exception;

/**
 * Exception levée lorsqu'un abonnement n'est pas trouvé
 */
public class SubscriptionNotFoundException extends RuntimeException {
    
    public SubscriptionNotFoundException(String message) {
        super(message);
    }
    
    public SubscriptionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

