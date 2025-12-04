package org.example.facade;

import org.example.model.TraineeDTO;
import org.example.model.TrainerDTO;
import org.example.model.TrainingDTO;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private GymFacade gymFacade;

    @Test
    void createTrainee_ShouldCallService() {
        TraineeDTO traineeDTO = new TraineeDTO();
        gymFacade.createTrainee(traineeDTO);
        verify(traineeService).createTrainee(traineeDTO);
    }

    @Test
    void createTrainer_ShouldCallService() {
        TrainerDTO trainerDTO = new TrainerDTO();
        gymFacade.createTrainer(trainerDTO);
        verify(trainerService).createTrainer(trainerDTO);
    }

    @Test
    void createTraining_ShouldCallService() {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingName("Strength");
        trainingDTO.setTrainerId(1L);
        trainingDTO.setTraineeId(2L);

        gymFacade.createTraining(trainingDTO);
        verify(trainingService).createTraining(trainingDTO);
    }

    @Test
    void getTraineeById_ShouldDelegateToService() {
        gymFacade.getTraineeById(5L);
        verify(traineeService).getTraineeById(5L);
    }

    @Test
    void getTrainingById_ShouldDelegateToService() {
        gymFacade.getTrainingById(3L);
        verify(trainingService).getTrainingById(3L);
    }

    @Test
    void getAllTrainees_ShouldCallService() {
        gymFacade.getAllTrainees();
        verify(traineeService).getAllTrainees();
    }

    @Test
    void getAllTrainers_ShouldCallService() {
        gymFacade.getAllTrainers();
        verify(trainerService).getAllTrainers();
    }
}
