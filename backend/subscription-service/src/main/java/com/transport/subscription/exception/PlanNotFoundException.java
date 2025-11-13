package com.transport.subscription.exception;

/**
 * Exception levée lorsqu'un plan n'est pas trouvé
 */
public class PlanNotFoundException extends RuntimeException {
    
    public PlanNotFoundException(String message) {
        super(message);
    }
    
    public PlanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

