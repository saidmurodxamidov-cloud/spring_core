package org.example.service;

import org.example.dao.TrainingDAO;
import org.example.model.TrainingDTO;
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
class TrainingDTOServiceTest {
    @Mock
    private TrainingDAO trainingDAO;
    @InjectMocks
    private TrainingService trainingService;


    private TrainingDTO trainingDTO1;
    private TrainingDTO trainingDTO2;

    @BeforeEach
    void setUp() {

        trainingDTO1 = new TrainingDTO();
        trainingDTO1.setTrainingId(1L);
        trainingDTO1.setTrainingName("Morning Cardio");
        trainingDTO1.setTraineeId(10L);
        trainingDTO1.setTrainerId(20L);
        trainingDTO1.setDate(LocalDate.now());
        trainingDTO1.setTrainingDuration(Duration.ofMinutes(60));

        trainingDTO2 = new TrainingDTO();
        trainingDTO2.setTrainingId(2L);
        trainingDTO2.setTrainingName("Evening Yoga");
        trainingDTO2.setTraineeId(11L);
        trainingDTO2.setTrainerId(21L);
        trainingDTO2.setDate(LocalDate.now().plusDays(1));
        trainingDTO2.setTrainingDuration(Duration.ofMinutes(45));
    }

    @Test
    void createTraining_ShouldInvokeDAO() {
        trainingService.createTraining(trainingDTO1);
        verify(trainingDAO, times(1)).create(trainingDTO1);
    }



    @Test
    void getTrainingById_ShouldReturnTrainingFromDAO() {
        when(trainingDAO.findById(1L)).thenReturn(trainingDTO1);

        TrainingDTO result = trainingService.getTrainingById(1L);

        assertNotNull(result);
        assertEquals(trainingDTO1.getTrainingId(), result.getTrainingId());
        assertEquals(trainingDTO1.getTrainingName(), result.getTrainingName());
        verify(trainingDAO, times(1)).findById(1L);
    }

    @Test
    void getTrainingById_ShouldReturnNull_WhenNotFound() {
        when(trainingDAO.findById(99L)).thenReturn(null);

        TrainingDTO result = trainingService.getTrainingById(99L);

        assertNull(result);
        verify(trainingDAO, times(1)).findById(99L);
    }

    @Test
    void getAllTrainings_ShouldReturnListFromDAO() {
        when(trainingDAO.findAll()).thenReturn(List.of(trainingDTO1, trainingDTO2));

        List<TrainingDTO> trainingDTOS = trainingService.getAllTrainings();

        assertEquals(2, trainingDTOS.size());
        assertTrue(trainingDTOS.contains(trainingDTO1));
        assertTrue(trainingDTOS.contains(trainingDTO2));
        verify(trainingDAO, times(1)).findAll();
    }

    @Test
    void getAllTrainings_ShouldReturnEmptyList_WhenNoTrainings() {
        when(trainingDAO.findAll()).thenReturn(List.of());

        List<TrainingDTO> trainingDTOS = trainingService.getAllTrainings();

        assertNotNull(trainingDTOS);
        assertTrue(trainingDTOS.isEmpty());
        verify(trainingDAO, times(1)).findAll();
    }

}
