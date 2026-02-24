package org.example.messageQueue;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

// org.example.messageQueue.WorkloadSender
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadSender {

    private final JmsTemplate jmsTemplate;

    @Value("${app.queue.workload}")
    private String workloadQueue;

    // @Retry      → if send throws, automatically retry up to 3x (configured in yml)
    // @CircuitBreaker → if too many failures, open the circuit and call fallback()
    //                   instead of keep trying — protects against a dead broker
    @Retry(name = "workloadService")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void sendWorkload(String idempotencyKey, TrainerWorkloadRequest request) {
        jmsTemplate.convertAndSend(workloadQueue, request, message -> {
            // idempotencyKey travels as a JMS property — same concept as an HTTP header
            // the consumer reads this to detect and skip duplicate messages
            message.setStringProperty("idempotencyKey", idempotencyKey);
            return message;
        });
        log.info("Workload sent. key={} action={}", idempotencyKey, request.getActionType());
    }

    // Resilience4j calls this automatically when circuit is OPEN
    // Signature must match sendWorkload() + Throwable as last param
    public void fallback(String idempotencyKey, TrainerWorkloadRequest request, Throwable ex) {
        log.error("Workload queue unavailable. key={} action={} reason={}",
                idempotencyKey, request.getActionType(), ex.getMessage());
        // for homework this is fine — in production you'd persist to an outbox table
    }
}