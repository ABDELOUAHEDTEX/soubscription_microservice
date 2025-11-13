package com.transport.subscription.exception;

/**
 * Exception levée lorsqu'une opération sur un abonnement est invalide
 */
public class InvalidSubscriptionException extends RuntimeException {
    
    public InvalidSubscriptionException(String message) {
        super(message);
    }
    
    public InvalidSubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

