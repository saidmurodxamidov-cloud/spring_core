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
public class WorkloadSenderService {

    private final WorkLoadClientService workLoadClientService;

    @Retry(name = "workloadService")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void sendWorkload(TrainerWorkloadRequest request) {

        workLoadClientService.sendWorkload(request);
        log.info("Workload sent successfully");
    }

    public void fallback(TrainerWorkloadRequest request, Throwable ex) {
        log.error("Workload service unavailable", ex);
    }

    @Retry(name = "workloadService")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void deleteWorkload(TrainerWorkloadRequest request) {
//        System.out.println(request + "service");
        workLoadClientService.deleteWorkload(request);
        log.info("Workload deleted successfully");
    }

}
