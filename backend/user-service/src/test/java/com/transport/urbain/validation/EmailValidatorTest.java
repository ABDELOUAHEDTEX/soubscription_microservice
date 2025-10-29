package com.transport.urbain.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the EmailValidator.
 * 
 * <p>Tests email address format validation including:
 * <ul>
 *   <li>Valid email acceptance</li>
 *   <li>Invalid email rejection</li>
 *   <li>Null and empty email handling</li>
 *   <li>Various email format variations</li>
 * </ul>
 * 
 * <p>Verifies:
 * <ul>
 *   <li>Proper email structure (local@domain)</li>
 *   <li>Allowed characters in local and domain parts</li>
 *   <li>Rejection of malformed addresses</li>
 * </ul>
 */
class EmailValidatorTest {

    private EmailValidator emailValidator;

    @BeforeEach
    void setUp() {
        emailValidator = new EmailValidator();
    }

    @Test
    void shouldReturnTrueForValidEmail() {
        // Given
        String validEmail = "user@example.com";

        // When
        boolean isValid = emailValidator.isValid(validEmail);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldReturnFalseForNullEmail() {
        // When
        boolean isValid = emailValidator.isValid(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseForEmptyEmail() {
        // When
        boolean isValid = emailValidator.isValid("");

        // Then
        assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "user@example.com",
        "test.user@example.co.uk",
        "firstname.lastname@example.com",
        "user123@example-domain.com",
        "user+tag@example.com",
        "user_name@example.org",
        "user.name@subdomain.example.com",
        "user123@example123.com"
    })
    void shouldReturnTrueForValidEmails(String validEmail) {
        // When
        boolean isValid = emailValidator.isValid(validEmail);

        // Then
        assertThat(isValid).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-email",
        "@example.com",
        "user@",
        "user name@example.com",
        "@",
        "user @example.com",
        "invalid email",
        "test@",
        " @example.com"
    })
    void shouldReturnFalseForInvalidEmails(String invalidEmail) {
        // When
        boolean isValid = emailValidator.isValid(invalidEmail);

        // Then
        assertThat(isValid).isFalse();
    }
}

