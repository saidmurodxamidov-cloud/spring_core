package org.example.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Data
@ConfigurationProperties(prefix = "spring.profiles")
public class EnvironmentInfo {

    /**
     * Active Spring profile names (e.g. {@code local}, {@code dev}, {@code component-test}).
     *
     * <p>Stored as strings (not {@link Profile} enum) so test-only profiles don't break
     * configuration-properties binding.
     */
    private List<String> active;
    
    @Autowired
    private Environment environment;

    @PostConstruct
    public void logEnvironmentInfo() {
        if (active == null || active.isEmpty()) {
            String[] activeProfiles = environment.getActiveProfiles();
            active = java.util.Arrays.stream(activeProfiles).toList();
        }

        String[] defaultProfiles = environment.getDefaultProfiles();

        log.info("=================================================");
        log.info("🚀 APPLICATION STARTED");
        log.info("=================================================");
        log.info("Active Profiles: {}", active);
        log.info("Default Profiles: {}", java.util.Arrays.toString(defaultProfiles));
        log.info("Database URL: {}", environment.getProperty("spring.datasource.url"));
        log.info("Server Port: {}", environment.getProperty("server.port"));
        log.info("JWT Expiration: {} ms", environment.getProperty("jwt.expiration"));
        log.info("=================================================");
    }

    private Set<String> activeUpper() {
        if (active == null) return Set.of();
        return active.stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim().toUpperCase())
                .collect(Collectors.toSet());
    }

    public boolean isLocal() {
        return activeUpper().contains(Profile.LOCAL.name());
    }

    public boolean isDev() {
        return activeUpper().contains(Profile.DEV.name());
    }

    public boolean isStaging() {
        return activeUpper().contains(Profile.STG.name());
    }

    public boolean isProduction() {
        return activeUpper().contains(Profile.PROD.name());
    }
}