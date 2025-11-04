package org.example.model;

import lombok.*;
import org.example.enums.TrainingType;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Training {
    private Long trainingId;
    private Long traineeId ;
    private Long trainerId;
    private LocalDate date;
    private String trainingName;
    private TrainingType trainingType;
    private Duration trainingDuration;
}


