package org.example.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvironmentInfo {

    private final Environment environment;

    @PostConstruct
    public void logEnvironmentInfo() {
        String[] activeProfiles = environment.getActiveProfiles();
        String[] defaultProfiles = environment.getDefaultProfiles();

        log.info("=================================================");
        log.info("🚀 APPLICATION STARTED");
        log.info("=================================================");
        log.info("Active Profiles: {}", Arrays.toString(activeProfiles));
        log.info("Default Profiles: {}", Arrays.toString(defaultProfiles));
        log.info("Database URL: {}", environment.getProperty("spring.datasource.url"));
        log.info("Server Port: {}", environment.getProperty("server.port"));
        log.info("JWT Expiration: {} ms", environment.getProperty("jwt.expiration"));
        log.info("=================================================");
    }

    public boolean isLocal() {
        return Arrays.asList(environment.getActiveProfiles()).contains("local");
    }

    public boolean isDev() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }

    public boolean isStaging() {
        return Arrays.asList(environment.getActiveProfiles()).contains("stg");
    }

    public boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }
}