package com.java.admin.modules.system.dto;

import com.java.admin.testutil.AbstractValidationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DeleteUsersRequestDTO Validation Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>@NotEmpty validation on ids list</li>
 *   <li>@Size validation on batch size (1-100)</li>
 *   <li>@NotBlank validation on each ID</li>
 * </ul>
 *
 * <p>Coverage Target: 100%
 */
@DisplayName("DeleteUsersRequestDTO Validation Tests")
class DeleteUsersRequestDTOTest extends AbstractValidationTest {

    @Test
    @DisplayName("Should validate successfully with valid ids list")
    void shouldValidateSuccessfullyWithValidIds() {
        // Given
        DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
        dto.setIds(List.of("id1", "id2", "id3"));

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when ids list is null")
    void shouldFailValidationWhenIdsIsNull() {
        // Given
        DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
        dto.setIds(null);

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .hasSize(1)
                .anyMatch(v -> v.getMessage().contains("IDs cannot be empty"));
    }

    @Test
    @DisplayName("Should fail validation when ids list is empty")
    void shouldFailValidationWhenIdsIsEmpty() {
        // Given
        DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
        dto.setIds(Collections.emptyList());

        // When
        var violations = validate(dto);

        // Then
        // Both @NotEmpty and @Size(min=1) violations are triggered
        assertThat(violations)
                .hasSizeGreaterThanOrEqualTo(1)
                .anyMatch(v -> v.getMessage().contains("IDs cannot be empty") ||
                               v.getMessage().contains("Batch size"));
    }

    @Test
    @DisplayName("Should fail validation when ids list exceeds maximum size")
    void shouldFailValidationWhenIdsExceedsMaximum() {
        // Given
        DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
        dto.setIds(java.util.stream.IntStream.range(0, 101)
                .mapToObj(i -> "id" + i)
                .toList());

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("Batch size must be between 1 and 100"));
    }

    @Test
    @DisplayName("Should validate successfully with exactly 100 ids")
    void shouldValidateSuccessfullyWithExactly100Ids() {
        // Given
        DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
        dto.setIds(java.util.stream.IntStream.range(0, 100)
                .mapToObj(i -> "id" + i)
                .toList());

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when ids list contains blank string")
    void shouldFailValidationWhenIdsContainsBlank() {
        // Given
        DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
        dto.setIds(List.of("id1", "", "id3"));

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("ID cannot be blank"));
    }

    @Test
    @DisplayName("Should fail validation when ids list contains null")
    void shouldFailValidationWhenIdsContainsNull() {
        // Given
        DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
        List<String> idsWithNull = new ArrayList<>();
        idsWithNull.add("id1");
        idsWithNull.add(null);
        idsWithNull.add("id3");
        dto.setIds(idsWithNull);

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("ID cannot be blank"));
    }

    @Test
    @DisplayName("Should fail validation when ids list contains only whitespace")
    void shouldFailValidationWhenIdsContainsWhitespace() {
        // Given
        DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
        dto.setIds(List.of("id1", "   ", "id3"));

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("ID cannot be blank"));
    }

    @Test
    @DisplayName("Should validate successfully with single id")
    void shouldValidateSuccessfullyWithSingleId() {
        // Given
        DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
        dto.setIds(List.of("id1"));

        // When
        var violations = validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }
}
