package org.example.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.client.ActionType;

import java.time.LocalDate;

@Data
@Builder
public class TrainerWorkloadRequest {
    private String idempotencyKey;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean active;
    private LocalDate trainingDate;
    private int duration;

    private ActionType actionType;
}
