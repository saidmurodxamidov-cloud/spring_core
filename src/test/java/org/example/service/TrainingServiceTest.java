package org.example.service;

import org.example.dao.TrainingDAO;
import org.example.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TrainingDAO trainingDAO;
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingDAO = mock(TrainingDAO.class);
        trainingService = new TrainingService(trainingDAO);
    }

    @Test
    void createTraining_ShouldInvokeDAO() {
        Training training = new Training();
        training.setTrainingId(1L);
        training.setTrainingName("Morning Cardio");
        training.setTraineeId(10L);
        training.setTrainerId(20L);
        training.setDate(LocalDate.now());
        training.setTrainingDuration(Duration.ofMinutes(60));

        trainingService.createTraining(training);

        verify(trainingDAO, times(1)).create(training);
    }

    @Test
    void createTraining_ShouldThrowException_WhenIdsAreNull() {
        Training training = new Training();
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(training));
    }
}
