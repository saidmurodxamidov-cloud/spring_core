package org.example.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
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


    public Trainee createTrainee(Trainee trainee) {
        log.info("Facade: Creating trainee {}", trainee.getFirstName());
        traineeService.createTrainee(trainee);
        log.info("Facade: Trainee created with username: {}",
                trainee.getUserName());
        return trainee;
    }

    public void updateTrainee(Trainee trainee) {
        log.info("Facade: Updating trainee ID {}", trainee.getUserId());
        traineeService.updateTrainee(trainee);
    }

    public void deleteTrainee(Long traineeId) {
        log.info("Facade: Deleting trainee ID {}", traineeId);
        traineeService.deleteTrainee(traineeId);
    }

    public Trainee getTraineeById(Long traineeId) {
        log.info("Facade: Getting trainee by ID {}", traineeId);
        return traineeService.getTraineeById(traineeId);
    }

    public List<Trainee> getAllTrainees() {
        log.info("Facade: Fetching all trainees");
        return traineeService.getAllTrainees();
    }


    public Trainer createTrainer(Trainer trainer) {
        log.info("Facade: Creating trainer {}", trainer.getFirstName());
        trainerService.createTrainer(trainer);
        log.info("Facade: Trainer created with username: {} and password: {}",
                trainer.getUserName(), trainer.getPassword());
        return trainer;
    }

    public void updateTrainer(Trainer trainer) {
        log.info("Facade: Updating trainer ID {}", trainer.getUserId());
        trainerService.updateTrainer(trainer);
    }

    public Trainer getTrainerById(Long trainerId) {
        log.info("Facade: Getting trainer by ID {}", trainerId);
        return trainerService.getTrainerById(trainerId);
    }

    public List<Trainer> getAllTrainers() {
        log.info("Facade: Fetching all trainers");
        return trainerService.getAllTrainers();
    }


    public void createTraining(Training training) {
        log.info("Facade: Creating training '{}'", training.getTrainingName());
        trainingService.createTraining(training);
    }

    public Training getTrainingById(Long trainingId) {
        log.info("Facade: Getting training by ID {}", trainingId);
        return trainingService.getTrainingById(trainingId);
    }

    public List<Training> getAllTrainings() {
        log.info("Facade: Fetching all trainings");
        return trainingService.getAllTrainings();
    }
}
