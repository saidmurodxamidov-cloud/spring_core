package org.example.mq;

import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdempotencyKeyService {
    public String generateKey(TrainerWorkloadRequest request) {
        String raw = String.join("|",
                request.getUsername(),
                String.valueOf(request.getTrainingDate()),
                request.getActionType().name()
        );
        return UUID.nameUUIDFromBytes(raw.getBytes()).toString();
    }
}