package com.transport.subscription.exception;

/**
 * Exception levée lorsqu'un paiement échoue auprès de la passerelle.
 */
public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message) {
        super(message);
    }

    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
