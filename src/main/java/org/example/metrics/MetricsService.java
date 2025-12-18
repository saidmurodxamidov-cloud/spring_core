package org.example.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter registrationCounter;
    private final Counter trainingCreatedCounter;

    private final Timer loginTimer;
    private final Timer trainingCreationTimer;

    private final AtomicInteger activeUsersGauge;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.loginSuccessCounter = Counter.builder("auth.login.success")
                .description("Number of successful logins")
                .tag("type", "authentication")
                .register(meterRegistry);

        this.loginFailureCounter = Counter.builder("auth.login.failure")
                .description("Number of failed login attempts")
                .tag("type", "authentication")
                .register(meterRegistry);

        this.registrationCounter = Counter.builder("user.registration")
                .description("Number of user registrations")
                .tag("type", "user")
                .register(meterRegistry);

        this.trainingCreatedCounter = Counter.builder("training.created")
                .description("Number of trainings created")
                .tag("type", "training")
                .register(meterRegistry);

        this.loginTimer = Timer.builder("auth.login.duration")
                .description("Time taken for login process")
                .tag("type", "authentication")
                .register(meterRegistry);

        this.trainingCreationTimer = Timer.builder("training.creation.duration")
                .description("Time taken to create a training")
                .tag("type", "training")
                .register(meterRegistry);

        this.activeUsersGauge = new AtomicInteger(0);
        Gauge.builder("users.active.count", activeUsersGauge, AtomicInteger::get)
                .description("Number of currently active users")
                .tag("type", "user")
                .register(meterRegistry);
    }

    public void incrementLoginSuccess() {
        loginSuccessCounter.increment();
        log.debug("Login success counter incremented");
    }

    public void incrementLoginFailure() {
        loginFailureCounter.increment();
        log.debug("Login failure counter incremented");
    }

    public void incrementRegistration() {
        registrationCounter.increment();
        log.debug("Registration counter incremented");
    }

    public void incrementTrainingCreated() {
        trainingCreatedCounter.increment();
        log.debug("Training created counter incremented");
    }

    public Timer.Sample startLoginTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordLoginDuration(Timer.Sample sample) {
        sample.stop(loginTimer);
    }

    public Timer.Sample startTrainingCreationTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordTrainingCreationDuration(Timer.Sample sample) {
        sample.stop(trainingCreationTimer);
    }

    public void incrementActiveUsers() {
        activeUsersGauge.incrementAndGet();
        log.debug("Active users: {}", activeUsersGauge.get());
    }

    public void decrementActiveUsers() {
        activeUsersGauge.decrementAndGet();
        log.debug("Active users: {}", activeUsersGauge.get());
    }

    public int getActiveUsers() {
        return activeUsersGauge.get();
    }

    public void recordTrainingByType(String trainingType) {
        Counter.builder("training.by.type")
                .description("Number of trainings by type")
                .tag("training_type", trainingType)
                .register(meterRegistry)
                .increment();
    }

    public void recordError(String errorType) {
        Counter.builder("application.errors")
                .description("Application errors")
                .tag("error_type", errorType)
                .register(meterRegistry)
                .increment();
    }
}