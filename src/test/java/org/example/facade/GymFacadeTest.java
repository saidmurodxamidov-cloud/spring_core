package org.example.facade;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createTrainee_ShouldCallService() {
        Trainee trainee = new Trainee();
        gymFacade.createTrainee(trainee);
        verify(traineeService).createTrainee(trainee);
    }

    @Test
    void createTrainer_ShouldCallService() {
        Trainer trainer = new Trainer();
        gymFacade.createTrainer(trainer);
        verify(trainerService).createTrainer(trainer);
    }

    @Test
    void createTraining_ShouldCallService() {
        Training training = new Training();
        training.setTrainingName("Strength");
        training.setTrainerId(1L);
        training.setTraineeId(2L);
        gymFacade.createTraining(training);
        verify(trainingService).createTraining(training);
    }
}
