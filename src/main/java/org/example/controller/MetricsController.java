package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.metrics.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/active-users")
    public ResponseEntity<Map<String, Integer>> getActiveUsers() {
        Map<String, Integer> response = new HashMap<>();
        response.put("activeUsers", metricsService.getActiveUsers());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test/increment-login-success")
    public ResponseEntity<String> testIncrementLoginSuccess() {
        metricsService.incrementLoginSuccess();
        return ResponseEntity.ok("Login success counter incremented");
    }

    @PostMapping("/test/increment-login-failure")
    public ResponseEntity<String> testIncrementLoginFailure() {
        metricsService.incrementLoginFailure();
        return ResponseEntity.ok("Login failure counter incremented");
    }

    @PostMapping("/test/record-training/{type}")
    public ResponseEntity<String> testRecordTraining(@PathVariable String type) {
        metricsService.recordTrainingByType(type);
        return ResponseEntity.ok("Training recorded for type: " + type);
    }
}