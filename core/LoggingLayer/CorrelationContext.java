package com.enterprise.automation.core.logging;

import java.util.UUID;

/**
 * Thread-safe correlation context for tracing requests across layers
 */
public class CorrelationContext {

    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();
    private static final ThreadLocal<String> testName = new ThreadLocal<>();
    private static final ThreadLocal<String> stepName = new ThreadLocal<>();

    /**
     * Initialize correlation context for a test
     */
    public static void initialize(String test) {
        correlationId.set(generateCorrelationId());
        testName.set(test);
        stepName.set(null);
    }

    /**
     * Set current step name
     */
    public static void setStepName(String step) {
        stepName.set(step);
    }

    /**
     * Get correlation ID
     */
    public static String getCorrelationId() {
        return correlationId.get();
    }

    /**
     * Get test name
     */
    public static String getTestName() {
        return testName.get();
    }

    /**
     * Get current step name
     */
    public static String getStepName() {
        return stepName.get();
    }

    /**
     * Clear correlation context (call in test cleanup)
     */
    public static void clear() {
        correlationId.remove();
        testName.remove();
        stepName.remove();
    }

    /**
     * Generate unique correlation ID
     */
    private static String generateCorrelationId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Get full context as string
     */
    public static String getFullContext() {
        return String.format("Test=%s, CorrelationId=%s, Step=%s",
                getTestName(), getCorrelationId(), getStepName());
    }
}
