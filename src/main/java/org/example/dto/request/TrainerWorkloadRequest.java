package org.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mq.ActionType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor  // Essential for Jackson deserialization
@AllArgsConstructor
public class TrainerWorkloadRequest {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean active;
    private LocalDate trainingDate;
    private int duration;
    private ActionType actionType;
}
