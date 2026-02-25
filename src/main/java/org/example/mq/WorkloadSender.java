package org.example.mq;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadSender {

    private final JmsTemplate jmsTemplate;
    private final IdempotencyKeyService idempotencyKeyService;
    private final JmsMessageEnricher enricher;


    @Value("${app.queue.workload}")
    private String workloadQueue;

    @Retry(name = "workloadService")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void sendWorkload(TrainerWorkloadRequest request) {
        String idempotencyKey = idempotencyKeyService.generateKey(request);
        jmsTemplate.convertAndSend(workloadQueue, request, message -> {
            message.setStringProperty("idempotencyKey", idempotencyKey);
            enricher.enrich(message,idempotencyKey);
            return message;
        });
        log.info("Workload sent. key={} action={}", idempotencyKey, request.getActionType());
    }

    public void fallback(TrainerWorkloadRequest request, Throwable ex) {
        log.error("Workload queue unavailable. action={} reason={}",
                 request.getActionType(), ex.getMessage());
    }
}