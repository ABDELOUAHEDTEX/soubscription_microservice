package com.transport.urbain.model;

/**
 * Enum representing gender options for user profiles.
 * 
 * <p>Provides inclusive options for users to specify their gender identity:
 * <ul>
 *   <li>MALE - Male gender</li>
 *   <li>FEMALE - Female gender</li>
 *   <li>OTHER - Other gender identity</li>
 *   <li>PREFER_NOT_TO_SAY - Users who prefer not to disclose</li>
 * </ul>
 * 
 * <p>This field is optional and used for personalization and demographic
 * purposes while respecting user privacy preferences.
 */
public enum Gender {
    /** Male gender */
    MALE,
    
    /** Female gender */
    FEMALE,
    
    /** Other gender identity */
    OTHER,
    
    /** User prefers not to disclose gender */
    PREFER_NOT_TO_SAY
}
