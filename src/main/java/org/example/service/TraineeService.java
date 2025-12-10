package org.example.service;
import org.example.model.TraineeDTO;

public interface TraineeService {

    TraineeDTO createTrainee(TraineeDTO traineeDTO);

    boolean authenticate(String username, String password);

    TraineeDTO getTraineeByUsername(String username);
}
