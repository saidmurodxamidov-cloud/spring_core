package org.example.model;

import org.example.enums.TrainingType;

import java.time.LocalDate;

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

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public Integer getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Integer trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
