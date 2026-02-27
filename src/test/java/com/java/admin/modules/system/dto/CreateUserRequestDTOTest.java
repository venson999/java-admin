package com.java.admin.modules.system.dto;

import com.java.admin.testutil.AbstractValidationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CreateUserRequestDTO Validation Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>@NotBlank validation on username and password</li>
 *   <li>@Size validation on username and password length</li>
 *   <li>@Pattern validation on password complexity</li>
 *   <li>@Email validation on email format</li>
 * </ul>
 *
 * <p>Coverage Target: 100%
 */
@DisplayName("CreateUserRequestDTO Validation Tests")
class CreateUserRequestDTOTest extends AbstractValidationTest {

    @Test
    @DisplayName("Should validate successfully with valid data")
    void shouldValidateSuccessfullyWithValidData() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("Password123");
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when username is null")
    void shouldFailValidationWhenUsernameIsNull() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername(null);
        dto.setPassword("Password123");
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Username is required"));
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void shouldFailValidationWhenUsernameIsBlank() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("   ");
        dto.setPassword("Password123");
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Username is required"));
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void shouldFailValidationWhenUsernameIsTooShort() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("ab");  // less than 3
        dto.setPassword("Password123");
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Username length must be between 3 and 30"));
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void shouldFailValidationWhenUsernameIsTooLong() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("a".repeat(31));  // more than 30
        dto.setPassword("Password123");
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Username length must be between 3 and 30"));
    }

    @Test
    @DisplayName("Should fail validation when password is null")
    void shouldFailValidationWhenPasswordIsNull() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword(null);
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Password is required"));
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void shouldFailValidationWhenPasswordIsBlank() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("   ");
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Password is required"));
    }

    @Test
    @DisplayName("Should fail validation when password is too short")
    void shouldFailValidationWhenPasswordIsTooShort() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("Pass1");  // less than 8
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Password length must be between 8 and 20"));
    }

    @Test
    @DisplayName("Should fail validation when password is too long")
    void shouldFailValidationWhenPasswordIsTooLong() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("a".repeat(21));  // more than 20
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Password length must be between 8 and 20"));
    }

    @Test
    @DisplayName("Should fail validation when password lacks uppercase")
    void shouldFailValidationWhenPasswordLacksUppercase() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("password123");  // no uppercase
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Password must contain at least one uppercase letter"));
    }

    @Test
    @DisplayName("Should fail validation when password lacks lowercase")
    void shouldFailValidationWhenPasswordLacksLowercase() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("PASSWORD123");  // no lowercase
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Password must contain at least one uppercase letter, one lowercase letter, and one digit"));
    }

    @Test
    @DisplayName("Should fail validation when password lacks digit")
    void shouldFailValidationWhenPasswordLacksDigit() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("Passwordabc");  // no digit
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Password must contain at least one") &&
                               v.getMessage().contains("digit"));
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("Password123");
        dto.setEmail("invalid-email");  // invalid format

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should validate successfully when email is null")
    void shouldValidateSuccessfullyWhenEmailIsNull() {
        // Given - email is optional
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("Password123");
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
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("Password123");
        dto.setEmail("");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate successfully with valid email format")
    void shouldValidateSuccessfullyWithValidEmailFormat() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("Password123");
        dto.setEmail("user.name+tag@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation with multiple violations")
    void shouldFailValidationWithMultipleViolations() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("ab");  // too short
        dto.setPassword("pass");  // too short, no complexity
        dto.setEmail("invalid");  // invalid email

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .hasSizeGreaterThanOrEqualTo(3)
                .anyMatch(v -> v.getMessage().contains("Username"))
                .anyMatch(v -> v.getMessage().contains("Password"))
                .anyMatch(v -> v.getMessage().contains("email"));
    }

    @Test
    @DisplayName("Should validate successfully with minimum valid username length")
    void shouldValidateSuccessfullyWithMinimumUsernameLength() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("abc");  // exactly 3
        dto.setPassword("Password123");
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate successfully with maximum valid username length")
    void shouldValidateSuccessfullyWithMaximumUsernameLength() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("a".repeat(30));  // exactly 30
        dto.setPassword("Password123");
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate successfully with minimum valid password length")
    void shouldValidateSuccessfullyWithMinimumPasswordLength() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("Pass1234");  // exactly 8
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate successfully with maximum valid password length")
    void shouldValidateSuccessfullyWithMaximumPasswordLength() {
        // Given
        CreateUserRequestDTO dto = new CreateUserRequestDTO();
        dto.setUsername("alice");
        dto.setPassword("Password12345678901");  // exactly 20
        dto.setEmail("alice@example.com");

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }
}
