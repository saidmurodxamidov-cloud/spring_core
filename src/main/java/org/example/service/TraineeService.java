package org.example.service;
import org.example.model.TraineeDTO;

public interface TraineeService {

    TraineeDTO createTrainee(TraineeDTO traineeDTO);

    TraineeDTO getTraineeByUsername(String username);
}
