package org.example.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.TraineeDTO;
import org.example.model.TrainerDTO;
import org.example.model.TrainingDTO;
import org.example.service.impl.TraineeServiceImpl;
import org.example.service.impl.TrainerServiceImpl;
import org.example.service.impl.TrainingServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GymFacade {

    private final TraineeServiceImpl traineeServiceImpl;
    private final TrainerServiceImpl trainerServiceImpl;
    private final TrainingServiceImpl trainingServiceImpl;


    public TraineeDTO createTrainee(TraineeDTO traineeDTO) {
        log.debug("Facade: Creating trainee {}", traineeDTO.getFirstName());
        traineeServiceImpl.createTrainee(traineeDTO);
        log.info("Facade: Trainee created with username: {}",
                traineeDTO.getUserName());
        return traineeDTO;
    }

    public void updateTrainee(TraineeDTO traineeDTO) {
        log.info("Facade: Updating trainee ID {}", traineeDTO.getUserId());
        traineeServiceImpl.updateTrainee(traineeDTO);
    }

    public void deleteTrainee(Long traineeId) {
        log.info("Facade: Deleting trainee ID {}", traineeId);
        traineeServiceImpl.deleteTrainee(traineeId);
    }

    public TraineeDTO getTraineeById(Long traineeId) {
        log.debug("Facade: Getting trainee by ID {}", traineeId);
        return traineeServiceImpl.getTraineeById(traineeId);
    }

    public List<TraineeDTO> getAllTrainees() {
        log.debug("Facade: Fetching all trainees");
        return traineeServiceImpl.getAllTrainees();
    }


    public TrainerDTO createTrainer(TrainerDTO trainerDTO) {
        log.debug("Facade: Creating trainer {}", trainerDTO.getFirstName());
        trainerServiceImpl.createTrainer(trainerDTO);
        log.info("Facade: Trainer created with username: {} and password: {}",
                trainerDTO.getUserName(), trainerDTO.getPassword());
        return trainerDTO;
    }

    public void updateTrainer(TrainerDTO trainerDTO) {
        log.info("Facade: Updating trainer ID {}", trainerDTO.getUserId());
        trainerServiceImpl.updateTrainer(trainerDTO);
    }

    public TrainerDTO getTrainerById(Long trainerId) {
        log.debug("Facade: Getting trainer by ID {}", trainerId);
        return trainerServiceImpl.getTrainerById(trainerId);
    }

    public List<TrainerDTO> getAllTrainers() {
        log.debug("Facade: Fetching all trainers");
        return trainerServiceImpl.getAllTrainers();
    }


    public void createTraining(TrainingDTO trainingDTO) {
        log.info("Facade: Creating training '{}'", trainingDTO.getTrainingName());
        trainingServiceImpl.createTraining(trainingDTO);
    }

    public TrainingDTO getTrainingById(Long trainingId) {
        log.debug("Facade: Getting training by ID {}", trainingId);
        return trainingServiceImpl.getTrainingById(trainingId);
    }

    public List<TrainingDTO> getAllTrainings() {
        log.debug("Facade: Fetching all trainings");
        return trainingServiceImpl.getAllTrainings();
    }
}
