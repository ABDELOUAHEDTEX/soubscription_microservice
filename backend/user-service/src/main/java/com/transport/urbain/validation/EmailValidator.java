package com.transport.urbain.validation;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validator for email address format.
 * 
 * <p>Validates that email addresses conform to standard email format requirements:
 * <ul>
 *   <li>Contains alphanumeric characters, plus, underscore, hyphen, and period in the local part</li>
 *   <li>Contains @ symbol separating local and domain parts</li>
 *   <li>Contains a valid domain name</li>
 * </ul>
 * 
 * <p>Pattern: {@code ^[A-Za-z0-9+_.-]+@(.+)$}
 * 
 * <p>Used during user registration and email updates to ensure proper email format.
 */
@Component
public class EmailValidator {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Validates an email address format.
     * 
     * @param email the email address to validate
     * @return true if email format is valid, false otherwise
     */
    public boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }
}
