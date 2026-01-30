package com.java.admin.testutil;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple in-memory Logback appender for testing log output
 *
 * <p>Usage:
 * <pre>{@code
 * InMemoryAppender appender = new InMemoryAppender();
 * Logger logger = (Logger) LoggerFactory.getLogger(YourClass.class);
 * logger.addAppender(appender);
 * logger.setLevel(Level.INFO);
 *
 * // Execute code that logs
 * yourClass.method();
 *
 * // Verify logs
 * assertThat(appender.getOutput()).contains("expected message");
 *
 * // Cleanup
 * logger.detachAppender(appender);
 * }</pre>
 */
public class InMemoryAppender extends AppenderBase<ILoggingEvent> {

    private final List<String> logMessages = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent event) {
        logMessages.add(event.getFormattedMessage());
    }

    /**
     * Get all captured log messages
     */
    public List<String> getLogMessages() {
        return new ArrayList<>(logMessages);
    }

    /**
     * Get all captured output as a single string
     */
    public String getOutput() {
        return String.join("\n", logMessages);
    }

    /**
     * Clear all captured messages
     */
    public void clear() {
        logMessages.clear();
    }
}
