package org.example.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.TraineeDTO;
import org.example.model.TrainerDTO;
import org.example.model.TrainingDTO;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;


    public TraineeDTO createTrainee(TraineeDTO traineeDTO) {
        log.debug("Facade: Creating trainee {}", traineeDTO.getFirstName());
        traineeService.createTrainee(traineeDTO);
        log.info("Facade: Trainee created with username: {}",
                traineeDTO.getUserName());
        return traineeDTO;
    }

    public void updateTrainee(TraineeDTO traineeDTO) {
        log.info("Facade: Updating trainee ID {}", traineeDTO.getUserId());
        traineeService.updateTrainee(traineeDTO);
    }

    public void deleteTrainee(Long traineeId) {
        log.info("Facade: Deleting trainee ID {}", traineeId);
        traineeService.deleteTrainee(traineeId);
    }

    public TraineeDTO getTraineeById(Long traineeId) {
        log.debug("Facade: Getting trainee by ID {}", traineeId);
        return traineeService.getTraineeById(traineeId);
    }

    public List<TraineeDTO> getAllTrainees() {
        log.debug("Facade: Fetching all trainees");
        return traineeService.getAllTrainees();
    }


    public TrainerDTO createTrainer(TrainerDTO trainerDTO) {
        log.debug("Facade: Creating trainer {}", trainerDTO.getFirstName());
        trainerService.createTrainer(trainerDTO);
        log.info("Facade: Trainer created with username: {} and password: {}",
                trainerDTO.getUserName(), trainerDTO.getPassword());
        return trainerDTO;
    }

    public void updateTrainer(TrainerDTO trainerDTO) {
        log.info("Facade: Updating trainer ID {}", trainerDTO.getUserId());
        trainerService.updateTrainer(trainerDTO);
    }

    public TrainerDTO getTrainerById(Long trainerId) {
        log.debug("Facade: Getting trainer by ID {}", trainerId);
        return trainerService.getTrainerById(trainerId);
    }

    public List<TrainerDTO> getAllTrainers() {
        log.debug("Facade: Fetching all trainers");
        return trainerService.getAllTrainers();
    }


    public void createTraining(TrainingDTO trainingDTO) {
        log.info("Facade: Creating training '{}'", trainingDTO.getTrainingName());
        trainingService.createTraining(trainingDTO);
    }

    public TrainingDTO getTrainingById(Long trainingId) {
        log.debug("Facade: Getting training by ID {}", trainingId);
        return trainingService.getTrainingById(trainingId);
    }

    public List<TrainingDTO> getAllTrainings() {
        log.debug("Facade: Fetching all trainings");
        return trainingService.getAllTrainings();
    }
}
