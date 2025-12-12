package com.enterprise.automation.core.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

/**
 * Framework logger with correlation context support
 */
public class FrameworkLogger {
    private final Logger logger;

    public FrameworkLogger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }

    public static FrameworkLogger getLogger(Class<?> clazz) {
        return new FrameworkLogger(clazz);
    }

    public void info(String message) {
        logger.info(addContext(message));
    }

    public void info(String message, Object... args) {
        logger.info(addContext(message), args);
    }

    public void debug(String message) {
        logger.debug(addContext(message));
    }

    public void debug(String message, Object... args) {
        logger.debug(addContext(message), args);
    }

    public void warn(String message) {
        logger.warn(addContext(message));
    }

    public void warn(String message, Throwable throwable) {
        logger.warn(addContext(message), throwable);
    }

    public void error(String message) {
        logger.error(addContext(message));
    }

    public void error(String message, Throwable throwable) {
        logger.error(addContext(message), throwable);
    }

    public void step(String stepDescription) {
        logger.info("STEP: " + addContext(stepDescription));
    }

    public void apiCall(String method, String endpoint) {
        logger.info("API CALL: {} {}", method, endpoint);
    }

    public void apiResponse(int statusCode, long responseTime) {
        logger.info("API RESPONSE: Status={}, Time={}ms", statusCode, responseTime);
    }

    private String addContext(String message) {
        String correlationId = CorrelationContext.getCorrelationId();
        String testName = CorrelationContext.getTestName();
        
        if (correlationId != null || testName != null) {
            return String.format("[%s][%s] %s", 
                testName != null ? testName : "N/A",
                correlationId != null ? correlationId : "N/A",
                message);
        }
        return message;
    }
}