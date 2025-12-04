package org.example.service.dao;

import org.example.dao.TrainingDAO;
import org.example.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    @Mock
    private TrainingDAO trainingDAO;
    @InjectMocks
    private TrainingService trainingService;


    private Training training1;
    private Training training2;

    @BeforeEach
    void setUp() {

        training1 = new Training();
        training1.setTrainingId(1L);
        training1.setTrainingName("Morning Cardio");
        training1.setTraineeId(10L);
        training1.setTrainerId(20L);
        training1.setDate(LocalDate.now());
        training1.setTrainingDuration(Duration.ofMinutes(60));

        training2 = new Training();
        training2.setTrainingId(2L);
        training2.setTrainingName("Evening Yoga");
        training2.setTraineeId(11L);
        training2.setTrainerId(21L);
        training2.setDate(LocalDate.now().plusDays(1));
        training2.setTrainingDuration(Duration.ofMinutes(45));
    }

    @Test
    void createTraining_ShouldInvokeDAO() {
        trainingService.createTraining(training1);
        verify(trainingDAO, times(1)).create(training1);
    }



    @Test
    void getTrainingById_ShouldReturnTrainingFromDAO() {
        when(trainingDAO.findById(1L)).thenReturn(training1);

        Training result = trainingService.getTrainingById(1L);

        assertNotNull(result);
        assertEquals(training1.getTrainingId(), result.getTrainingId());
        assertEquals(training1.getTrainingName(), result.getTrainingName());
        verify(trainingDAO, times(1)).findById(1L);
    }

    @Test
    void getTrainingById_ShouldReturnNull_WhenNotFound() {
        when(trainingDAO.findById(99L)).thenReturn(null);

        Training result = trainingService.getTrainingById(99L);

        assertNull(result);
        verify(trainingDAO, times(1)).findById(99L);
    }

    @Test
    void getAllTrainings_ShouldReturnListFromDAO() {
        when(trainingDAO.findAll()).thenReturn(List.of(training1, training2));

        List<Training> trainings = trainingService.getAllTrainings();

        assertEquals(2, trainings.size());
        assertTrue(trainings.contains(training1));
        assertTrue(trainings.contains(training2));
        verify(trainingDAO, times(1)).findAll();
    }

    @Test
    void getAllTrainings_ShouldReturnEmptyList_WhenNoTrainings() {
        when(trainingDAO.findAll()).thenReturn(List.of());

        List<Training> trainings = trainingService.getAllTrainings();

        assertNotNull(trainings);
        assertTrue(trainings.isEmpty());
        verify(trainingDAO, times(1)).findAll();
    }

}
