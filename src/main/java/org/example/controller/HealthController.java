package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.EnvironmentInfo;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final Environment environment;
    private final EnvironmentInfo environmentInfo;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("profile", Arrays.toString(environment.getActiveProfiles()));
        health.put("environment", getEnvironmentType());

        return ResponseEntity.ok(health);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", environment.getProperty("spring.application.name"));
        info.put("profiles", Arrays.toString(environment.getActiveProfiles()));
        info.put("port", environment.getProperty("server.port"));
        info.put("database", maskDatabaseUrl(environment.getProperty("spring.datasource.url")));
        info.put("isLocal", environmentInfo.isLocal());
        info.put("isDev", environmentInfo.isDev());
        info.put("isStaging", environmentInfo.isStaging());
        info.put("isProduction", environmentInfo.isProduction());

        return ResponseEntity.ok(info);
    }

    private String getEnvironmentType() {
        if (environmentInfo.isLocal()) return "LOCAL";
        if (environmentInfo.isDev()) return "DEVELOPMENT";
        if (environmentInfo.isStaging()) return "STAGING";
        if (environmentInfo.isProduction()) return "PRODUCTION";
        return "UNKNOWN";
    }

    private String maskDatabaseUrl(String url) {
        if (url == null) return "N/A";

        return url.replaceAll("://.*@", "://***:***@");
    }
}