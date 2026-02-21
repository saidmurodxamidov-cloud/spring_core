package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingResponse {
    private Long id;
    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private Integer durationInMinutes;
    private String traineeUsername;

}
