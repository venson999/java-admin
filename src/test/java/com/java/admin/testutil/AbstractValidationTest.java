package com.java.admin.testutil;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Set;

/**
 * Base class for DTO validation tests
 *
 * <p>Test classes that validate Bean Validation annotations should extend this base for:
 * <ul>
 *   <li>Validator instance</li>
 *   <li>Common validation methods</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 * class DeleteUsersRequestDTOTest extends AbstractValidationTest {
 *     @Test
 *     void shouldValidateSuccessfully() {
 *         DeleteUsersRequestDTO dto = new DeleteUsersRequestDTO();
 *         dto.setIds(List.of("id1"));
 *         var violations = validate(dto);
 *         assertThat(violations).isEmpty();
 *     }
 * }
 * }</pre>
 */
public abstract class AbstractValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    /**
     * Validate an object and return constraint violations
     *
     * @param <T> the type of the object to validate
     * @param obj the object to validate
     * @return set of constraint violations
     */
    protected <T> Set<ConstraintViolation<T>> validate(T obj) {
        return validator.validate(obj);
    }
}
