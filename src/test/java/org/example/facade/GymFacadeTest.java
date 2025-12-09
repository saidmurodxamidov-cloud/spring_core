package org.example.facade;

import org.example.model.TraineeDTO;
import org.example.model.TrainerDTO;
import org.example.model.TrainingDTO;
import org.example.service.impl.TraineeServiceImpl;
import org.example.service.impl.TrainerServiceImpl;
import org.example.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeServiceImpl traineeServiceImpl;

    @Mock
    private TrainerServiceImpl trainerServiceImpl;

    @Mock
    private TrainingServiceImpl trainingServiceImpl;

    @InjectMocks
    private GymFacade gymFacade;

    @Test
    void createTrainee_ShouldCallService() {
        TraineeDTO traineeDTO = new TraineeDTO();
        gymFacade.createTrainee(traineeDTO);
        verify(traineeServiceImpl).createTrainee(traineeDTO);
    }

    @Test
    void createTrainer_ShouldCallService() {
        TrainerDTO trainerDTO = new TrainerDTO();
        gymFacade.createTrainer(trainerDTO);
        verify(trainerServiceImpl).createTrainer(trainerDTO);
    }

    @Test
    void createTraining_ShouldCallService() {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingName("Strength");
        trainingDTO.setTrainerId(1L);
        trainingDTO.setTraineeId(2L);

        gymFacade.createTraining(trainingDTO);
        verify(trainingServiceImpl).createTraining(trainingDTO);
    }

    @Test
    void getTraineeById_ShouldDelegateToService() {
        gymFacade.getTraineeById(5L);
        verify(traineeServiceImpl).getTraineeById(5L);
    }

    @Test
    void getTrainingById_ShouldDelegateToService() {
        gymFacade.getTrainingById(3L);
        verify(trainingServiceImpl).getTrainingById(3L);
    }

    @Test
    void getAllTrainees_ShouldCallService() {
        gymFacade.getAllTrainees();
        verify(traineeServiceImpl).getAllTrainees();
    }

    @Test
    void getAllTrainers_ShouldCallService() {
        gymFacade.getAllTrainers();
        verify(trainerServiceImpl).getAllTrainers();
    }
}
