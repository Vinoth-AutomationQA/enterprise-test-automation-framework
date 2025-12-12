package com.enterprise.automation.core.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration resolver with priority-based value resolution.
 * Resolution order: CI Variable → Secrets → Environment File → Default
 */
public class ConfigResolver {
    private static Properties config = new Properties();
    private static SecretsProvider secretsProvider = new SecretsProvider();
    private static boolean initialized = false;

    static {
        loadConfiguration();
    }

    /**
     * Load configuration from multiple sources with priority order
     */
    private static void loadConfiguration() {
        if (initialized) {
            return;
        }

        String environment = System.getProperty("env", "dev");
        
        // Load default properties first (lowest priority)
        loadPropertiesFile("config/default.properties");
        
        // Load environment-specific properties (overrides defaults)
        loadPropertiesFile("config/" + environment + ".properties");
        
        // CI/System variables have highest priority
        System.getProperties().forEach((key, value) -> {
            if (key.toString().startsWith("app.") || 
                key.toString().startsWith("db.") || 
                key.toString().startsWith("api.")) {
                config.put(key, value);
            }
        });

        initialized = true;
        System.out.println("Configuration loaded for environment: " + environment);
    }

    /**
     * Load properties from classpath resource file
     */
    private static void loadPropertiesFile(String fileName) {
        try (InputStream input = ConfigResolver.class.getClassLoader()
                .getResourceAsStream(fileName)) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                config.putAll(props);
                System.out.println("Loaded config file: " + fileName);
            } else {
                System.out.println("Config file not found: " + fileName + " (skipping)");
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to load config: " + fileName);
        }
    }

    /**
     * Get configuration value with priority resolution
     * @param key Configuration key
     * @return Configuration value
     * @throws IllegalArgumentException if key not found
     */
    public static String get(String key) {
        // 1. Check CI/System environment variable (highest priority)
        String ciValue = System.getProperty(key);
        if (ciValue != null && !ciValue.isEmpty()) {
            return ciValue;
        }
        
        String envValue = System.getenv(key.replace(".", "_").toUpperCase());
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        
        // 2. Check secrets vault for sensitive data
        if (isSensitiveKey(key)) {
            String secretValue = secretsProvider.getSecret(key);
            if (secretValue != null && !secretValue.isEmpty()) {
                return secretValue;
            }
        }
        
        // 3. Check loaded properties (env file + defaults)
        String fileValue = config.getProperty(key);
        if (fileValue != null && !fileValue.isEmpty()) {
            return fileValue;
        }
        
        throw new IllegalArgumentException("Configuration key not found: " + key);
    }

    /**
     * Get configuration value with default fallback
     */
    public static String get(String key, String defaultValue) {
        try {
            return get(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    /**
     * Get integer configuration value
     */
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Get integer configuration value with default
     */
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get boolean configuration value
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * Get boolean configuration value with default
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(get(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Check if key represents sensitive data
     */
    private static boolean isSensitiveKey(String key) {
        String lowerKey = key.toLowerCase();
        return lowerKey.contains("password") || 
               lowerKey.contains("token") || 
               lowerKey.contains("secret") ||
               lowerKey.contains("apikey") ||
               lowerKey.contains("credential");
    }

    /**
     * Get current environment name
     */
    public static String getEnvironment() {
        return System.getProperty("env", "dev");
    }

    /**
     * Force reload configuration (useful for testing)
     */
    public static void reload() {
        config.clear();
        initialized = false;
        loadConfiguration();
    }
}