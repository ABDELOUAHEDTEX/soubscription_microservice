package com.transport.urbain.model;

/**
 * Enum representing the possible status states of a user account.
 * 
 * <p>Defines the lifecycle states a user account can be in:
 * <ul>
 *   <li>ACTIVE - User account is active and can fully use the system</li>
 *   <li>INACTIVE - User account is inactive (user hasn't logged in recently)</li>
 *   <li>SUSPENDED - User account is suspended by administrator</li>
 *   <li>PENDING_VERIFICATION - User account is pending email/phone verification</li>
 *   <li>DELETED - User account is soft-deleted and should not be accessible</li>
 * </ul>
 * 
 * <p>The status is used to control user access to the system and track
 * account lifecycle throughout the user journey.
 */
public enum UserStatus {
    /** Account is active and operational */
    ACTIVE,
    
    /** Account is inactive */
    INACTIVE,
    
    /** Account is suspended by administrator */
    SUSPENDED,
    
    /** Account is awaiting verification */
    PENDING_VERIFICATION,
    
    /** Account is soft-deleted */
    DELETED
}
