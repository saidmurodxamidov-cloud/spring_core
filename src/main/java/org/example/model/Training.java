package org.example.model;

import lombok.*;

import java.time.Duration;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Training {
    private Long trainingId;
    private Long traineeId ;
    private Long trainerId;
    private LocalDate date;
    private String trainingName;
    private TrainingType trainingType;
    private Duration trainingDuration;
}
