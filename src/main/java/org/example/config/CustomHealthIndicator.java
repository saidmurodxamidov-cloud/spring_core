package org.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.persistence.repository.UserRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom health indicator that provides application health status.
 * This replaces the HealthController and integrates with Spring Boot Actuator.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {

    private final UserRepository userRepository;
    private final Environment environment;
    private final EnvironmentInfo environmentInfo;

    @Override
    public Health health() {
        try {
            // Check database connectivity
            userRepository.count();
            
            // Build health details
            Map<String, Object> details = new HashMap<>();
            details.put("timestamp", LocalDateTime.now());
            details.put("profiles", Arrays.toString(environment.getActiveProfiles()));
            details.put("environment", getEnvironmentType());
            details.put("database", "UP");
            
            return Health.up()
                    .withDetails(details)
                    .build();
        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        }
    }

    private String getEnvironmentType() {
        if (environmentInfo.isLocal()) return "LOCAL";
        if (environmentInfo.isDev()) return "DEVELOPMENT";
        if (environmentInfo.isStaging()) return "STAGING";
        if (environmentInfo.isProduction()) return "PRODUCTION";
        return "UNKNOWN";
    }
}

