package org.example.service;

import org.example.dto.request.TrainerRegistrationRequest;
import org.example.dto.response.AuthResponse;
import org.example.persistence.model.TrainerDTO;
import java.util.List;

public interface TrainerService {

    AuthResponse createTrainer(TrainerRegistrationRequest trainerDTO);

    TrainerDTO getTrainerByUsername(String username);

    TrainerDTO updateTrainer(TrainerDTO trainerDto);

    List<TrainerDTO> getTrainersNotAssignedToTrainee(String traineeUsername);
}
