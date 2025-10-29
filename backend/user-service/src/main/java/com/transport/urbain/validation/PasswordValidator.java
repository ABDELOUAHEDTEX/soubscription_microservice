package com.transport.urbain.validation;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validator for password strength and format.
 * 
 * <p>Validates that passwords meet security requirements:
 * <ul>
 *   <li>At least 8 characters long</li>
 *   <li>Contains at least one digit (0-9)</li>
 *   <li>Contains at least one lowercase letter (a-z)</li>
 *   <li>Contains at least one uppercase letter (A-Z)</li>
 *   <li>Contains at least one special character (@#$%^&+=!?)</li>
 *   <li>No whitespace allowed</li>
 * </ul>
 * 
 * <p>Used during user registration and password changes to ensure
 * strong password security standards.
 */
@Component
public class PasswordValidator {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?])(?=\\S+$).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    /**
     * Validates a password against security requirements.
     * 
     * @param password the password to validate
     * @return true if password meets all requirements, false otherwise
     */
    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return pattern.matcher(password).matches();
    }

    /**
     * Returns a human-readable description of password requirements.
     * 
     * @return string describing the password requirements
     */
    public String getRequirements() {
        return "Password must contain at least 8 characters, one digit, " +
                "one lowercase letter, one uppercase letter, and one special character (@#$%^&+=!?)";
    }
}
