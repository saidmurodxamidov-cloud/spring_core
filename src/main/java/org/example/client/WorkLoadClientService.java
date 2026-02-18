package org.example.client;

import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "workload-service"
)


public interface WorkLoadClientService {

    @PostMapping("/api/workloads")
    void sendWorkload(@RequestBody TrainerWorkloadRequest request);

    @DeleteMapping("/api/workloads")
    void deleteWorkload(@RequestBody TrainerWorkloadRequest request);
}
