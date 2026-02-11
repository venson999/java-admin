package com.java.admin.infrastructure.aspect;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.java.admin.infrastructure.model.Result;
import com.java.admin.testutil.InMemoryAppender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * ControllerLogAspect Unit Tests
 *
 * <p>Test Coverage:
 * <ul>
 *   <li>Normal request/response logging</li>
 *   <li>Slow response detection (>1000ms)</li>
 *   <li>Exception handling and logging</li>
 *   <li>Null RequestAttributes handling</li>
 *   <li>Log level verification</li>
 * </ul>
 *
 * <p>Coverage Target: 90%+
 */
@DisplayName("ControllerLogAspect Unit Tests")
@ExtendWith(MockitoExtension.class)
class ControllerLogAspectTest {

    private ControllerLogAspect aspect;
    private InMemoryAppender appender;
    private Logger logger;

    private MockedStatic<RequestContextHolder> mockedRequestContextHolder;

    @BeforeEach
    void setUp() {
        aspect = new ControllerLogAspect();

        // Setup logger capture
        logger = (Logger) org.slf4j.LoggerFactory.getLogger(ControllerLogAspect.class);
        appender = new InMemoryAppender();
        appender.setContext((LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory());
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.DEBUG); // Enable DEBUG to capture all logs

        // Initialize MockedStatic
        mockedRequestContextHolder = mockStatic(RequestContextHolder.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedRequestContextHolder != null) {
            mockedRequestContextHolder.close();
        }
        if (appender != null) {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    @DisplayName("Should log request and response for normal execution")
    void shouldLogRequestAndResponseForNormalExecution() throws Throwable {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        // Mock ProceedingJoinPoint
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("TestController");
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn(Result.success("OK"));

        // When
        Object result = aspect.logController(joinPoint);

        // Then
        assertThat(result).isNotNull();
        assertThat(appender.getOutput()).contains("Request started");
        assertThat(appender.getOutput()).contains("GET");
        assertThat(appender.getOutput()).contains("/api/test");
        assertThat(appender.getOutput()).contains("TestController");
        assertThat(appender.getOutput()).contains("testMethod");
    }

    @Test
    @DisplayName("Should log slow response when execution time exceeds 1000ms")
    void shouldLogSlowResponseWhenExecutionTimeExceeds1000ms() throws Throwable {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/slow");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("SlowController");
        when(signature.getName()).thenReturn("slowMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(1100); // Simulate slow operation
            return Result.success("Done");
        });

        // When
        Object result = aspect.logController(joinPoint);

        // Then
        assertThat(result).isNotNull();
        assertThat(appender.getOutput()).contains("Slow response");
        assertThat(appender.getOutput()).contains("POST");
        assertThat(appender.getOutput()).contains("/api/slow");
    }

    @Test
    @DisplayName("Should log exception when controller throws exception")
    void shouldLogExceptionWhenControllerThrowsException() throws Throwable {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("DELETE");
        request.setRequestURI("/api/error");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        RuntimeException exception = new RuntimeException("Database error");
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("ErrorController");
        when(signature.getName()).thenReturn("errorMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});
        when(joinPoint.proceed()).thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> aspect.logController(joinPoint))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        assertThat(appender.getOutput()).contains("Controller exception");
        assertThat(appender.getOutput()).contains("DELETE");
        assertThat(appender.getOutput()).contains("/api/error");
        assertThat(appender.getOutput()).contains("Database error");
    }

    @Test
    @DisplayName("Should proceed without logging when RequestAttributes is null")
    void shouldProceedWithoutLoggingWhenRequestAttributesIsNull() throws Throwable {
        // Given
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(null);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn(Result.success("Direct"));

        // When
        Object result = aspect.logController(joinPoint);

        // Then
        assertThat(result).isNotNull();
        verify(joinPoint, times(1)).proceed();
        // Should not have any request logs
        assertThat(appender.getOutput()).doesNotContain("Request started");
    }

    @Test
    @DisplayName("Should log method arguments in debug mode")
    void shouldLogMethodArgumentsInDebugMode() throws Throwable {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/args");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("ArgsController");
        when(signature.getName()).thenReturn("methodWithArgs");
        Object[] args = {"arg1", 123, true};
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(Result.success());

        // When
        aspect.logController(joinPoint);

        // Then
        assertThat(appender.getOutput()).contains("Args:");
    }

    @Test
    @DisplayName("Should log execution time in milliseconds")
    void shouldLogExecutionTimeInMilliseconds() throws Throwable {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("PUT");
        request.setRequestURI("/api/timing");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("TimingController");
        when(signature.getName()).thenReturn("timingMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(100);
            return Result.success();
        });

        // When
        aspect.logController(joinPoint);

        // Then
        assertThat(appender.getOutput()).contains("ms");
        assertThat(appender.getOutput()).contains("Cost:");
    }

    @Test
    @DisplayName("Should handle controller with no arguments")
    void shouldHandleControllerWithNoArguments() throws Throwable {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/noargs");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("NoArgsController");
        when(signature.getName()).thenReturn("noArgsMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn(Result.success());

        // When
        Object result = aspect.logController(joinPoint);

        // Then
        assertThat(result).isNotNull();
        assertThat(appender.getOutput()).contains("Request started");
    }

    @Test
    @DisplayName("Should preserve exception original type")
    void shouldPreserveExceptionOriginalType() throws Throwable {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/exception");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        NullPointerException npe = new NullPointerException("Null value");
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("TestController");
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenThrow(npe);

        // When & Then
        assertThatThrownBy(() -> aspect.logController(joinPoint))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null value");
    }

    @Test
    @DisplayName("Should log different HTTP methods correctly")
    void shouldLogDifferentHttpMethodsCorrectly() throws Throwable {
        // Given
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};

        for (String method : methods) {
            // Reset appender for each iteration
            appender.clear();

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod(method);
            request.setRequestURI("/api/test");

            ServletRequestAttributes attributes = new ServletRequestAttributes(request);
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

            ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
            Signature signature = mock(Signature.class);
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getDeclaringTypeName()).thenReturn("TestController");
            when(signature.getName()).thenReturn("testMethod");
            when(joinPoint.getArgs()).thenReturn(new Object[]{});
            when(joinPoint.proceed()).thenReturn(Result.success());

            // When
            aspect.logController(joinPoint);

            // Then
            assertThat(appender.getOutput()).contains(method);
        }
    }

    @Test
    @DisplayName("Should handle very long URI")
    void shouldHandleVeryLongUri() throws Throwable {
        // Given
        String longUri = "/api/very/long/path/" + "a".repeat(1000);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI(longUri);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("TestController");
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn(Result.success());

        // When
        Object result = aspect.logController(joinPoint);

        // Then
        assertThat(result).isNotNull();
        assertThat(appender.getOutput()).contains(longUri);
    }

    @Test
    @DisplayName("Should handle URI with query parameters")
    void shouldHandleUriWithQueryParameters() throws Throwable {
        // Given
        String uriWithQuery = "/api/search?name=test&page=1&size=10";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI(uriWithQuery);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("TestController");
        when(signature.getName()).thenReturn("searchMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn(Result.success());

        // When
        aspect.logController(joinPoint);

        // Then
        assertThat(appender.getOutput()).contains(uriWithQuery);
    }

    @Test
    @DisplayName("Should handle special characters in URI")
    void shouldHandleSpecialCharactersInUri() throws Throwable {
        // Given
        String specialUri = "/api/test/中文%20space?param=value%2Ftest";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI(specialUri);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("TestController");
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn(Result.success());

        // When
        Object result = aspect.logController(joinPoint);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should log controller class name correctly")
    void shouldLogControllerClassNameCorrectly() throws Throwable {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("com.java.admin.modules.system.controller.SysUserController");
        when(signature.getName()).thenReturn("getUserInfo");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn(Result.success());

        // When
        aspect.logController(joinPoint);

        // Then
        assertThat(appender.getOutput()).contains("SysUserController");
        assertThat(appender.getOutput()).contains("getUserInfo");
    }

    @Test
    @DisplayName("Should return the same result as joinPoint.proceed()")
    void shouldReturnSameResultAsJoinPointProceed() throws Throwable {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

        Result<String> expected = Result.success("test-data");
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("TestController");
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn(expected);

        // When
        Object result = aspect.logController(joinPoint);

        // Then
        assertThat(result).isSameAs(expected);
    }
}
