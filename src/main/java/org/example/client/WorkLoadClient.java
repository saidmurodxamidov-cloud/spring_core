package org.example.client;

import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "workload-service"
)


public interface WorkLoadClient {

    @PostMapping("/api/workloads")
    void sendWorkload(@RequestHeader("Idempotency-Key") String idempotencyKey,
                      @RequestBody TrainerWorkloadRequest request);

//    @DeleteMapping("/api/workloads")
//    void deleteWorkload(@RequestBody TrainerWorkloadRequest request);
}
