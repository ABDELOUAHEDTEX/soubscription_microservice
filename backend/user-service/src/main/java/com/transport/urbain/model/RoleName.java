package com.transport.urbain.model;

/**
 * Enum representing the available roles in the system.
 * 
 * <p>Defines the standard roles that can be assigned to users:
 * <ul>
 *   <li>ADMIN - System administrator with full access</li>
 *   <li>PASSENGER - Regular user who uses the transport service</li>
 *   <li>DRIVER - Driver of transport vehicles</li>
 * </ul>
 * 
 * <p>Roles are used for role-based access control (RBAC) to restrict
 * access to resources and operations based on user responsibilities.
 */
public enum RoleName {
    /** Administrator role with full system access */
    ADMIN,
    
    /** Passenger role for regular users of the transport service */
    PASSENGER,
    
    /** Driver role for transport vehicle operators */
    DRIVER
}
