package org.example.service;

import org.example.dto.request.TrainerRequest;
import org.example.dto.request.TrainerTrainingsRequest;
import org.example.dto.request.TrainerUpdateRequest;
import org.example.dto.response.*;

import java.util.List;

public interface TrainerService {

    AuthResponse createTrainer(TrainerRequest trainerRequest);

    TrainerProfileResponse getTrainerProfile(String username);

    TrainerUpdateResponse updateTrainer(String username, TrainerUpdateRequest request);


    List<TrainingResponse> getTrainerTrainings(String username, TrainerTrainingsRequest request);
}
