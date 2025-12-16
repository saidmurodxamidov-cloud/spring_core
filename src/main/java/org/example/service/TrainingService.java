package org.example.service;
import org.example.dto.request.TrainingRequest;
import org.example.persistence.model.TrainingDTO;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {

    TrainingDTO createTraining(TrainingRequest trainingRequest);

    List<TrainingDTO> getAllTraineeTrainings(String username);

    List<TrainingDTO> getAllTrainerTrainings(String username);

    List<TrainingDTO> getTraineeTrainings(String username,
                                          LocalDate fromDate,
                                          LocalDate toDate,
                                          String trainerName,
                                          String trainingTypeName);

    List<TrainingDTO> getTrainerTrainings(String username,
                                          LocalDate fromDate,
                                          LocalDate toDate,
                                          String traineeName);
}
