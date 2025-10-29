package com.transport.urbain.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the PasswordValidator.
 * 
 * <p>Tests password strength validation including:
 * <ul>
 *   <li>Valid password acceptance</li>
 *   <li>Invalid password rejection</li>
 *   <li>Null and empty password handling</li>
 *   <li>Password requirements (length, complexity)</li>
 *   <li>Special character validation</li>
 * </ul>
 * 
 * <p>Verifies:
 * <ul>
 *   <li>Minimum 8 characters requirement</li>
 *   <li>Requirement for digit, lowercase, uppercase, and special character</li>
 *   <li>No whitespace allowed</li>
 *   <li>Requirements message generation</li>
 * </ul>
 */
class PasswordValidatorTest {

    private PasswordValidator passwordValidator;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator();
    }

    @Test
    void shouldReturnTrueForValidPassword() {
        // Given
        String validPassword = "Password@123";

        // When
        boolean isValid = passwordValidator.isValid(validPassword);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldReturnFalseForNullPassword() {
        // When
        boolean isValid = passwordValidator.isValid(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseForEmptyPassword() {
        // When
        boolean isValid = passwordValidator.isValid("");

        // Then
        assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "short",                    // Too short
        "12345678",                 // No letters
        "abcdefgh",                 // No uppercase, numbers, or special chars
        "ABCDEFGH",                 // No lowercase, numbers, or special chars
        "Password123",              // No special character
        "password@123",             // No uppercase letter
        "PASSWORD@123",             // No lowercase letter
        "Password@",                // No numbers
        "Pass@1",                   // Too short
        "Password 123"              // Contains space
    })
    void shouldReturnFalseForInvalidPasswords(String invalidPassword) {
        // When
        boolean isValid = passwordValidator.isValid(invalidPassword);

        // Then
        assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Password@123",
        "MyP@ssw0rd!",
        "Test123#$%",
        "Complex@Pass1",
        "User@2024Pass",
        "Stron9P@ss!"
    })
    void shouldReturnTrueForValidPasswords(String validPassword) {
        // When
        boolean isValid = passwordValidator.isValid(validPassword);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldGetRequirementsMessage() {
        // When
        String requirements = passwordValidator.getRequirements();

        // Then
        assertThat(requirements).isNotNull();
        assertThat(requirements).isNotEmpty();
        assertThat(requirements).contains("8 characters");
        assertThat(requirements).contains("digit");
        assertThat(requirements).contains("lowercase");
        assertThat(requirements).contains("uppercase");
        assertThat(requirements).contains("special character");
    }

    @Test
    void shouldAcceptPasswordsWithVariousSpecialCharacters() {
        // Given
        String[] specialChars = {"@", "#", "$", "%", "^", "&", "+", "=", "!", "?"};

        for (String specialChar : specialChars) {
            String password = "Password123" + specialChar;
            
            // When
            boolean isValid = passwordValidator.isValid(password);

            // Then
            assertThat(isValid).isTrue();
        }
    }
}

