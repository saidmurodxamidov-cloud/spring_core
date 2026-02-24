package org.example.messageQueue;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TrainerWorkloadRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class WorkloadSender {

    private final JmsTemplate jmsTemplate;
    private final Tracer tracer;

    public WorkloadSender(ObjectProvider<Tracer> tracerProvider, JmsTemplate jmsTemplate) {
        this.tracer = tracerProvider.getIfAvailable();
        this.jmsTemplate = jmsTemplate;
    }


    @Value("${app.queue.workload}")
    private String workloadQueue;

    @Retry(name = "workloadService")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void sendWorkload(String idempotencyKey, TrainerWorkloadRequest request) {
        jmsTemplate.convertAndSend(workloadQueue, request, message -> {
            message.setStringProperty("idempotencyKey", idempotencyKey);

            if (tracer != null && tracer.currentSpan() != null) {
                var ctx = Objects.requireNonNull(tracer.currentSpan()).context();
                message.setStringProperty("traceId", ctx.traceId());
                message.setStringProperty("spanId",  ctx.spanId());
            } else {
                String traceId = MDC.get("traceId");
                String spanId  = MDC.get("spanId");
                if (traceId != null) message.setStringProperty("traceId", traceId);
                if (spanId  != null) message.setStringProperty("spanId",  spanId);
            }
            return message;
        });
        log.info("Workload sent. key={} action={}", idempotencyKey, request.getActionType());
    }

    public void fallback(String idempotencyKey, TrainerWorkloadRequest request, Throwable ex) {
        log.error("Workload queue unavailable. key={} action={} reason={}",
                idempotencyKey, request.getActionType(), ex.getMessage());
    }
}