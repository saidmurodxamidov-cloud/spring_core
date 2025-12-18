package org.example.service;

import org.example.dto.request.TraineeRegistrationRequest;
import org.example.dto.request.TraineeTrainingRequest;
import org.example.dto.request.TraineeUpdateRequest;
import org.example.dto.response.*;

import java.util.List;

public interface TraineeService {

    AuthResponse createTrainee(TraineeRegistrationRequest traineeRequest);

    TraineeProfileResponse getTrainee(String username);

    TraineeUpdateResponse updateTrainee(String username, TraineeUpdateRequest request);

    void deleteTrainee(String username);

    List<TrainerResponse> updateTraineesTrainerList(String username, List<String> trainerUsernames);

    List<TrainerResponse> getActiveTrainersNotAssignedToTrainee(String username);


    List<TrainingResponse> getTraineeTrainings(String username, TraineeTrainingRequest request);
}
