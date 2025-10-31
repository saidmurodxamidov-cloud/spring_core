package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.enums.TrainingType;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
@ToString(callSuper = true)
public class Training {
    private Long traineeId  ;
    private Long trainerId;
    private LocalDate date;
    private String trainingName;
    private TrainingType trainingType;
    private Integer trainingDuration;

    public Training(Long traineeId, Long trainerId, LocalDate date, String trainingName, TrainingType trainingType, Integer trainingDuration) {
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.date = date;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDuration = trainingDuration;
    }
    public Training(){}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return Objects.equals(traineeId, training.traineeId) && Objects.equals(trainerId, training.trainerId) && Objects.equals(date, training.date) && Objects.equals(trainingName, training.trainingName) && trainingType == training.trainingType && Objects.equals(trainingDuration, training.trainingDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traineeId, trainerId, date, trainingName, trainingType, trainingDuration);
    }
}
