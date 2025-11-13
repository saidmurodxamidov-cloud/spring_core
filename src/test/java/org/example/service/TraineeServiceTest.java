package org.example.service;

import org.example.interfaces.TraineeDAO;
import org.example.model.Trainee;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUserId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("Test Street");
    }

    @Test
    void createTrainee_ShouldGenerateUsernameAndPassword_AndCallDAO() {
        when(traineeDAO.findAll()).thenReturn(List.of());

        try (
                MockedStatic<UsernameGenerator> usernameGenMock = mockStatic(UsernameGenerator.class);
                MockedStatic<PasswordGenerator> passwordGenMock = mockStatic(PasswordGenerator.class)
        ) {
            usernameGenMock.when(() ->
                    UsernameGenerator.generateUsername(eq("John"), eq("Doe"), any(Set.class))
            ).thenReturn("john.doe");

            passwordGenMock.when(PasswordGenerator::generatePassword).thenReturn("p@ssword");

            traineeService.createTrainee(trainee);

            assertEquals("john.doe", trainee.getUserName());
            assertEquals("p@ssword", trainee.getPassword());

            verify(traineeDAO).create(trainee);
        }
    }

    @Test
    void updateTrainee_ShouldCallDAOUpdate() {
        traineeService.updateTrainee(trainee);
        verify(traineeDAO, times(1)).update(trainee);
    }

    @Test
    void deleteTrainee_ShouldCallDAODelete() {
        traineeService.deleteTrainee(1L);
        verify(traineeDAO, times(1)).delete(1L);
    }

    @Test
    void getTraineeById_ShouldReturnFromDAO() {
        when(traineeDAO.findById(1L)).thenReturn(trainee);

        Trainee result = traineeService.getTraineeById(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(traineeDAO).findById(1L);
    }

    @Test
    void getAllTrainees_ShouldReturnListFromDAO() {
        when(traineeDAO.findAll()).thenReturn(List.of(trainee));

        List<Trainee> result = traineeService.getAllTrainees();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(traineeDAO).findAll();
    }
}
