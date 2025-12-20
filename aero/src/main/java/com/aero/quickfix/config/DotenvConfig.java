package com.aero.quickfix.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * Load environment variables from .env file at application startup.
 */
@Configuration
public class DotenvConfig {

    static {
        try {
            // Load .env file - search from user's home or current directory
            String[] possiblePaths = {
                System.getProperty("user.dir"),  // Current working directory
                System.getProperty("user.home"),  // User home directory
                "."  // Fallback to current directory
            };

            Dotenv dotenv = null;
            for (String path : possiblePaths) {
                try {
                    dotenv = Dotenv.configure()
                            .directory(path)
                            .filename(".env")
                            .ignoreIfMissing()
                            .load();
                    if (dotenv != null && !dotenv.entries().isEmpty()) {
                        System.err.println("✓ Loaded .env from: " + path);
                        break;
                    }
                } catch (Exception e) {
                    // Try next path
                }
            }

            if (dotenv != null) {
                // Set environment variables from .env
                dotenv.entries().forEach(entry -> {
                    System.setProperty(entry.getKey(), entry.getValue());
                    if (entry.getKey().contains("API_KEY")) {
                        String value = entry.getValue();
                        String preview = (value != null && value.length() > 4) 
                            ? value.substring(0, 4) + "..." 
                            : value;
                        System.err.println("✓ Loaded env var: " + entry.getKey() + " = " + preview);
                    }
                });
            } else {
                System.err.println("⚠ No .env file found, using system environment variables");
            }
        } catch (Exception e) {
            System.err.println("⚠ Error loading .env file: " + e.getMessage());
        }
    }
}
