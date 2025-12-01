package org.example.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Training {
    @NotNull
    private Long trainingId;
    @NotNull
    private Long traineeId ;
    @NotNull
    private Long trainerId;
    private LocalDate date;
    private String trainingName;
    private TrainingTypeDTO trainingType;
    @NotNull
    @PositiveOrZero
    private Duration trainingDuration;
}
