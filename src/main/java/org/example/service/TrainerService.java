package org.example.service;

import org.example.model.TrainerDTO;
import java.util.List;

public interface TrainerService {

    TrainerDTO createTrainer(TrainerDTO trainerDTO);

    TrainerDTO getTrainerByUsername(String username);

    TrainerDTO updateTrainer(TrainerDTO trainerDto);

    void setActiveStatus(String username, boolean active);

    List<TrainerDTO> getTrainersNotAssignedToTrainee(String traineeUsername);
}
