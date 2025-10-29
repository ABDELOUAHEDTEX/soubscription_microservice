package com.transport.urbain.model;

/**
 * Enum representing authentication providers supported by the system.
 * 
 * <p>Defines the different ways users can authenticate:
 * <ul>
 *   <li>LOCAL - Email/password authentication (default)</li>
 *   <li>GOOGLE - OAuth authentication via Google</li>
 *   <li>FACEBOOK - OAuth authentication via Facebook</li>
 *   <li>APPLE - OAuth authentication via Apple</li>
 * </ul>
 * 
 * <p>Used to track how users registered and logged in, which is important
 * for OAuth-specific functionality and account linking.
 */
public enum AuthProvider {
    /** Local authentication with email and password */
    LOCAL,
    
    /** Google OAuth authentication */
    GOOGLE,
    
    /** Facebook OAuth authentication */
    FACEBOOK,
    
    /** Apple OAuth authentication */
    APPLE
}
