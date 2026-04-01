package org.example.mq;

import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class IdempotencyKeyService {

    public String generateKey(
            TrainerWorkloadRequest request,
            String traineeUsername,
            String trainingName,
            Long trainingId) {
        String raw = String.join("|",
                nullSafe(request.getUsername()),
                request.getTrainingDate() != null ? request.getTrainingDate().toString() : "",
                request.getActionType() != null ? request.getActionType().name() : "",
                String.valueOf(request.getDuration()),
                nullSafe(traineeUsername),
                nullSafe(trainingName),
                trainingId != null ? Long.toString(trainingId) : ""
        );
        return UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static String nullSafe(String value) {
        return value != null ? value : "";
    }
}