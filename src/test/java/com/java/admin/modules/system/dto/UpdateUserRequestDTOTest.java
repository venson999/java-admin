package com.java.admin.modules.system.dto;

import com.java.admin.testutil.AbstractValidationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UpdateUserRequestDTO Validation Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>@Email validation on email format</li>
 *   <li>Optional email field validation</li>
 * </ul>
 *
 * <p>Coverage Target: 100%
 */
@DisplayName("UpdateUserRequestDTO Validation Tests")
class UpdateUserRequestDTOTest extends AbstractValidationTest {

    @Test
    @DisplayName("Should validate successfully with valid email")
    void shouldValidateSuccessfullyWithValidEmail() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("newemail@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate successfully when email is null")
    void shouldValidateSuccessfullyWhenEmailIsNull() {
        // Given - email is optional
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail(null);

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate successfully when email is empty")
    void shouldValidateSuccessfullyWhenEmailIsEmpty() {
        // Given - email is optional
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when email is blank")
    void shouldFailValidationWhenEmailIsBlank() {
        // Given - blank string is not a valid email format even though field is optional
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("   ");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should fail validation when email format is invalid")
    void shouldFailValidationWhenEmailFormatIsInvalid() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("invalid-email-format");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .hasSize(1)
                .anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should fail validation when email has no domain")
    void shouldFailValidationWhenEmailHasNoDomain() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("user@");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should fail validation when email has no local part")
    void shouldFailValidationWhenEmailHasNoLocalPart() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should fail validation when email has no @ symbol")
    void shouldFailValidationWhenEmailHasNoAtSymbol() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should validate successfully with various valid email formats")
    void shouldValidateSuccessfullyWithVariousValidEmailFormats() {
        // Given
        String[] validEmails = {
                "user@example.com",
                "user.name@example.com",
                "user+tag@example.com",
                "user123@test-domain.co.uk",
                "first.last@subdomain.example.com"
        };

        // When & Then
        for (String email : validEmails) {
            UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
            dto.setEmail(email);
            var violations = validate(dto);
            assertThat(violations)
                    .as("Email '%s' should be valid", email)
                    .isEmpty();
        }
    }

    @Test
    @DisplayName("Should fail validation with various invalid email formats")
    void shouldFailValidationWithVariousInvalidEmailFormats() {
        // Given
        String[] invalidEmails = {
                "invalid",
                "@example.com",
                "user@",
                "user@@example.com",
                "user @example.com",
                "user@exa mple.com"
        };

        // When & Then
        for (String email : invalidEmails) {
            UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
            dto.setEmail(email);
            var violations = validate(dto);
            assertThat(violations)
                    .as("Email '%s' should be invalid", email)
                    .isNotEmpty()
                    .anyMatch(v -> v.getMessage().contains("Invalid email format"));
        }
    }

    @Test
    @DisplayName("Should handle email with special characters")
    void shouldHandleEmailWithSpecialCharacters() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("user.name+tag+label@example-domain.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should handle email with numbers")
    void shouldHandleEmailWithNumbers() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("user123@example42.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation for email with consecutive dots")
    void shouldFailValidationForEmailWithConsecutiveDots() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("user..name@example.com");

        // When
        var violations = validate(dto);

        // Then
        // @Email annotation rejects consecutive dots
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should fail validation for email with trailing whitespace")
    void shouldFailValidationForEmailWithTrailingWhitespace() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("user@example.com ");

        // When
        var violations = validate(dto);

        // Then
        // @Email annotation rejects trailing whitespace
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should validate successfully with new email")
    void shouldValidateSuccessfullyWithNewEmail() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("updated@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should accept unchanged email")
    void shouldAcceptUnchangedEmail() {
        // Given
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setEmail("original@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }
}
