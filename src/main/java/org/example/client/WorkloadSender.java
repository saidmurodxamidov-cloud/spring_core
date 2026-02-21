package org.example.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadSender {

    private final WorkLoadClient workLoadClient;

    @Retry(name = "workloadService")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void sendWorkload(String idempotencyKey,TrainerWorkloadRequest request) {

        workLoadClient.sendWorkload(idempotencyKey,request);
        log.info("Workload sent successfully");
    }

    public void fallback(String idempotencyKey, TrainerWorkloadRequest request, Throwable ex) {
        log.error("Workload service unavailable. Action: {}, key: {}",
                request.getActionType(), idempotencyKey, ex);
    }
}
