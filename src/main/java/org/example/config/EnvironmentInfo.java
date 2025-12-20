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
import java.util.stream.Collectors;

@Slf4j
@Component
@Data
@ConfigurationProperties(prefix = "spring.profiles")
public class EnvironmentInfo {

    private List<Profile> active;
    
    @Autowired
    private Environment environment;

    @PostConstruct
    public void logEnvironmentInfo() {
        if (active == null || active.isEmpty()) {
            String[] activeProfiles = environment.getActiveProfiles();
            active = java.util.Arrays.stream(activeProfiles)
                    .map(profile -> {
                        try {
                            return Profile.valueOf(profile.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            log.warn("Unknown profile: {}, skipping", profile);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
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

    public boolean isLocal() {
        return active != null && active.contains(Profile.LOCAL);
    }

    public boolean isDev() {
        return active != null && active.contains(Profile.DEV);
    }

    public boolean isStaging() {
        return active != null && active.contains(Profile.STG);
    }

    public boolean isProduction() {
        return active != null && active.contains(Profile.PROD);
    }
}