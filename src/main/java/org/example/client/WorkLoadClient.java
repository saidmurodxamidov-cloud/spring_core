package org.example.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class WorkLoadClient {
    private final WebClient webClient;

    public WorkLoadClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }
    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    @Retry(name = "workloadService")
    public Mono<Void> sendWorkload(TrainerWorkloadRequest request) {
        return webClient.post()
                .uri("http://workload-service/api/workloads")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Workload sent successfully"));
    }

    public Mono<Void> fallback(TrainerWorkloadRequest request, Throwable ex) {
        log.error("Workload service unavailable", ex);
        return Mono.empty();
    }
}
