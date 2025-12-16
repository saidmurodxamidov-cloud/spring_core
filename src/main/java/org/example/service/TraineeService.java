package org.example.service;
import org.example.dto.request.TraineeRegistrationRequest;
import org.example.dto.response.AuthResponse;
import org.example.persistence.model.TraineeDTO;

import java.util.List;

public interface TraineeService {

    AuthResponse createTrainee(TraineeRegistrationRequest traineeDTO);

    TraineeDTO getTraineeByUsername(String username);

    TraineeDTO updateTrainee(String username, TraineeDTO updateDTO);

    void deleteByUsername(String username);

    void updateTrainersList(String traineeUsername, List<String> trainersUsernames);
}
