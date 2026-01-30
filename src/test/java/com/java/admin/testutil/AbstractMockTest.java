package com.java.admin.testutil;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Base class for unit tests requiring Mockito
 *
 * <p>Test classes that use {@code @Mock} or {@code @InjectMocks} should extend this base for:
 * <ul>
 *   <li>JUnit 5 support</li>
 *   <li>Mockito extension</li>
 *   <li>Common test configuration</li>
 * </ul>
 *
 * <p>Test classes that do NOT require Mockito (e.g., pure utility class tests) should NOT extend this base.
 *
 * <p>Usage:
 * <pre>{@code
 * @ExtendWith(MockitoExtension.class)
 * class UserServiceTest extends AbstractMockTest {
 *     @Mock
 *     private UserRepository userRepository;
 *
 *     @InjectMocks
 *     private UserService userService;
 *
 *     @Test
 *     void shouldReturnUserWhenExists() {
 *         // test code using mocks
 *     }
 * }
 * }</pre>
 *
 * @see org.mockito.Mock
 * @see org.mockito.InjectMocks
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractMockTest {
    // Base class empty, used for unified extension and future config
}
