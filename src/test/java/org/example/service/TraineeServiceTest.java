package org.example.service;

import org.example.dao.TraineeDAO;
import org.example.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    private TraineeDAO traineeDAO;
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeDAO = mock(TraineeDAO.class);
        traineeService = new TraineeService(traineeDAO);
    }

    @Test
    void createTrainee_ShouldGenerateUsernameAndPassword() {
        when(traineeDAO.findAll()).thenReturn(List.of());

        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("Test Street");


        traineeService.createTrainee(trainee);


        assertNotNull(trainee.getUserName(), "Username should be generated");
        assertNotNull(trainee.getPassword(), "Password should be generated");
        assertEquals("john.doe", trainee.getUserName());
        assertEquals(8, trainee.getPassword().length());

        verify(traineeDAO, times(1)).create(trainee);
    }

    @Test
    void deleteTrainee_ShouldInvokeDAO() {
        traineeService.deleteTrainee(5L);
        verify(traineeDAO).delete(5L);
    }
}
