package org.example.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadSender {

    private final WorkloadSenderDelegate delegate;
    private final IdempotencyKeyService idempotencyKeyService;

    public void sendWorkload(TrainerWorkloadRequest request) {
        String idempotencyKey = idempotencyKeyService.generateKey(request);
        delegate.sendWorkload(request, idempotencyKey);
    }
}