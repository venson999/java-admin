package com.java.admin.testutil;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Base class for unit tests
 *
 * <p>All unit test classes should extend this base for:
 * <ul>
 *   <li>JUnit 5 support</li>
 *   <li>Mockito extension</li>
 *   <li>Common test configuration</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 * class JwtUtilTest extends AbstractUnitTest {
 *     @Test
 *     void shouldReturnTokenWhenCredentialsValid() {
 *         // test code
 *     }
 * }
 * }</pre>
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractUnitTest {
    // Base class empty, used for unified extension and future config
}
