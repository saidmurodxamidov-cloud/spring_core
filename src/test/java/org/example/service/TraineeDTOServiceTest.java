package org.example.service;

import org.example.dao.TraineeDAO;
import org.example.model.TraineeDTO;
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
class TraineeDTOServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

    @InjectMocks
    private TraineeService traineeService;

    private TraineeDTO traineeDTO;

    @BeforeEach
    void setUp() {
        traineeDTO = new TraineeDTO();
        traineeDTO.setUserId(1L);
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");
        traineeDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeDTO.setAddress("Test Street");
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

            passwordGenMock.when(PasswordGenerator::generatePassword).thenReturn("p@ssword".toCharArray());

            traineeService.createTrainee(traineeDTO);

            assertEquals("john.doe", traineeDTO.getUserName());
            assertArrayEquals("p@ssword".toCharArray(), traineeDTO.getPassword());

            verify(traineeDAO).create(traineeDTO);
        }
    }

    @Test
    void updateTrainee_ShouldCallDAOUpdate() {
        traineeService.updateTrainee(traineeDTO);
        verify(traineeDAO, times(1)).update(traineeDTO);
    }

    @Test
    void deleteTrainee_ShouldCallDAODelete() {
        traineeService.deleteTrainee(1L);
        verify(traineeDAO, times(1)).delete(1L);
    }

    @Test
    void getTraineeById_ShouldReturnFromDAO() {
        when(traineeDAO.findById(1L)).thenReturn(traineeDTO);

        TraineeDTO result = traineeService.getTraineeById(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(traineeDAO).findById(1L);
    }

    @Test
    void getAllTrainees_ShouldReturnListFromDAO() {
        when(traineeDAO.findAll()).thenReturn(List.of(traineeDTO));

        List<TraineeDTO> result = traineeService.getAllTrainees();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(traineeDAO).findAll();
    }
}
