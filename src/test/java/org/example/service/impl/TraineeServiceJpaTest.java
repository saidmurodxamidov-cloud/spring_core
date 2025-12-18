package org.example.service.impl;

import org.example.dto.request.TraineeRegistrationRequest;
import org.example.dto.request.TraineeTrainingRequest;
import org.example.dto.request.TraineeUpdateRequest;
import org.example.dto.response.*;
import org.example.persistence.entity.*;
import org.example.persistence.repository.TraineeRepository;
import org.example.persistence.repository.TrainerRepository;
import org.example.persistence.repository.TrainingRepository;
import org.example.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceJpaTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TraineeServiceJpa traineeService;

    private TraineeRegistrationRequest registrationRequest;
    private TraineeUpdateRequest updateRequest;
    private TraineeTrainingRequest trainingRequest;
    private TraineeEntity trainee;
    private UserEntity user;
    private TrainerEntity trainer;
    private TrainingTypeEntity trainingType;

    @BeforeEach
    void setUp() {
        registrationRequest = new TraineeRegistrationRequest();
        registrationRequest.setFirstname("John");
        registrationRequest.setLastname("Doe");
        registrationRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        registrationRequest.setAddress("123 Main St");

        updateRequest = TraineeUpdateRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .address("456 Oak St")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .isActive(true)
                .build();

        trainingRequest = TraineeTrainingRequest.builder()
                .from(LocalDate.of(2024, 1, 1))
                .to(LocalDate.of(2024, 12, 31))
                .trainerName("Trainer1")
                .trainingType("Fitness")
                .build();

        user = UserEntity.builder()
                .userName("john.doe")
                .passwordHash("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .roles(new HashSet<>())
                .build();

        trainee = TraineeEntity.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .trainers(new HashSet<>())
                .trainings(new HashSet<>())
                .build();

        trainingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();

        trainer = TrainerEntity.builder()
                .user(UserEntity.builder()
                        .userName("trainer1")
                        .firstName("Trainer")
                        .lastName("One")
                        .build())
                .specializations(new HashSet<>(Arrays.asList(trainingType)))
                .build();
    }

    @Test
    void createTrainee_Success() {
        when(userRepository.findAllUserNames()).thenReturn(new HashSet<>());
        when(bcrypt.encode(anyString())).thenReturn("encodedPassword");
        when(traineeRepository.save(any(TraineeEntity.class))).thenReturn(trainee);

        AuthResponse response = traineeService.createTrainee(registrationRequest);

        assertNotNull(response);
        assertNotNull(response.getUsername());
        assertNotNull(response.getPassword());
        verify(traineeRepository, times(1)).save(any(TraineeEntity.class));
        verify(userRepository, times(1)).findAllUserNames();
    }

    @Test
    void createTrainee_WithExistingUsernames() {
        Set<String> existingUsernames = new HashSet<>(Arrays.asList("john.doe"));
        when(userRepository.findAllUserNames()).thenReturn(existingUsernames);
        when(bcrypt.encode(anyString())).thenReturn("encodedPassword");
        when(traineeRepository.save(any(TraineeEntity.class))).thenReturn(trainee);

        AuthResponse response = traineeService.createTrainee(registrationRequest);

        assertNotNull(response);
        verify(traineeRepository, times(1)).save(any(TraineeEntity.class));
    }

    @Test
    void createTrainee_WithNullAddress() {
        registrationRequest.setAddress(null);
        when(userRepository.findAllUserNames()).thenReturn(new HashSet<>());
        when(bcrypt.encode(anyString())).thenReturn("encodedPassword");
        when(traineeRepository.save(any(TraineeEntity.class))).thenReturn(trainee);

        AuthResponse response = traineeService.createTrainee(registrationRequest);

        assertNotNull(response);
        verify(traineeRepository, times(1)).save(any(TraineeEntity.class));
    }

    @Test
    void getTrainee_Success() {
        trainee.setTrainers(new HashSet<>(Arrays.asList(trainer)));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));

        TraineeProfileResponse response = traineeService.getTrainee("john.doe");

        assertNotNull(response);
        assertEquals("John", response.getFirstname());
        assertEquals("Doe", response.getLastname());
        assertTrue(response.isActive());
        assertEquals("123 Main St", response.getAddress());
        verify(traineeRepository, times(1)).findByUserUserName("john.doe");
    }

    @Test
    void getTrainee_NotFound() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> traineeService.getTrainee("unknown"));
        verify(traineeRepository, times(1)).findByUserUserName("unknown");
    }

    @Test
    void getTrainee_WithMultipleTrainers() {
        TrainerEntity trainer2 = TrainerEntity.builder()
                .user(UserEntity.builder()
                        .userName("trainer2")
                        .firstName("Second")
                        .lastName("Trainer")
                        .build())
                .specializations(new HashSet<>())
                .build();
        trainee.setTrainers(new HashSet<>(Arrays.asList(trainer, trainer2)));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));

        TraineeProfileResponse response = traineeService.getTrainee("john.doe");

        assertNotNull(response);
    }

    @Test
    void updateTrainee_Success() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));

        TraineeUpdateResponse response = traineeService.updateTrainee("john.doe", updateRequest);

        assertNotNull(response);
        assertEquals("john.doe", response.getUsername());
        assertEquals("Doe", response.getLastname());
        assertTrue(response.isActive());
        assertEquals("456 Oak St", response.getAddress());
        verify(traineeRepository, times(1)).findByUserUserName("john.doe");
    }

    @Test
    void updateTrainee_NotFound() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> traineeService.updateTrainee("unknown", updateRequest));
    }

    @Test
    void updateTrainee_WithNullAddress() {
        updateRequest.setAddress(null);
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));

        TraineeUpdateResponse response = traineeService.updateTrainee("john.doe", updateRequest);

        assertNotNull(response);
        assertEquals("123 Main St", response.getAddress());
    }

    @Test
    void updateTrainee_WithNullDateOfBirth() {
        updateRequest.setDateOfBirth(null);
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));

        TraineeUpdateResponse response = traineeService.updateTrainee("john.doe", updateRequest);

        assertNotNull(response);
        assertEquals(LocalDate.of(1990, 1, 1), response.getDateOfBirth());
    }

    @Test
    void deleteTrainee_Success() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        doNothing().when(traineeRepository).delete(any(TraineeEntity.class));

        traineeService.deleteTrainee("john.doe");

        verify(traineeRepository, times(1)).findByUserUserName("john.doe");
        verify(traineeRepository, times(1)).delete(trainee);
    }

    @Test
    void deleteTrainee_NotFound() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> traineeService.deleteTrainee("unknown"));
    }

    @Test
    void updateTraineesTrainerList_Success() {
        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer2");
        Set<TrainerEntity> trainers = new HashSet<>(Arrays.asList(trainer));

        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserNameIn(anyList())).thenReturn(trainers);
        when(traineeRepository.save(any(TraineeEntity.class))).thenReturn(trainee);

        List<TrainerResponse> response = traineeService.updateTraineesTrainerList("john.doe", trainerUsernames);

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(traineeRepository, times(1)).findByUserUserName("john.doe");
        verify(traineeRepository, times(1)).save(any(TraineeEntity.class));
    }

    @Test
    void updateTraineesTrainerList_TraineeNotFound() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> traineeService.updateTraineesTrainerList("unknown", Arrays.asList("trainer1")));
    }

    @Test
    void updateTraineesTrainerList_PartialTrainersFound() {
        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer2", "trainer3");
        Set<TrainerEntity> trainers = new HashSet<>(Arrays.asList(trainer));

        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserNameIn(anyList())).thenReturn(trainers);
        when(traineeRepository.save(any(TraineeEntity.class))).thenReturn(trainee);

        List<TrainerResponse> response = traineeService.updateTraineesTrainerList("john.doe", trainerUsernames);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getActiveTrainersNotAssignedToTrainee_Success() {
        List<TrainerEntity> trainers = Arrays.asList(trainer);
        when(trainerRepository.findTrainersNotAssignedToTrainee(anyString())).thenReturn(trainers);

        List<TrainerResponse> response = traineeService.getActiveTrainersNotAssignedToTrainee("john.doe");

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(trainerRepository, times(1)).findTrainersNotAssignedToTrainee("john.doe");
    }

    @Test
    void getActiveTrainersNotAssignedToTrainee_EmptyList() {
        when(trainerRepository.findTrainersNotAssignedToTrainee(anyString())).thenReturn(Collections.emptyList());

        List<TrainerResponse> response = traineeService.getActiveTrainersNotAssignedToTrainee("john.doe");

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getTraineeTrainings_Success() {
        TrainingEntity training = TrainingEntity.builder()
                .trainingName("Test Training")
                .date(LocalDate.now())
                .trainingType(trainingType)
                .trainingDuration(java.time.Duration.ofMinutes(60))
                .build();

        when(traineeRepository.existsByUserUserName(anyString())).thenReturn(true);
        when(trainingRepository.findTraineeTrainingsByCriteria(anyString(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(training));

        List<TrainingResponse> response = traineeService.getTraineeTrainings("john.doe", trainingRequest);

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(traineeRepository, times(1)).existsByUserUserName("john.doe");
        verify(trainingRepository, times(1))
                .findTraineeTrainingsByCriteria(anyString(), any(), any(), any(), any());
    }

    @Test
    void getTraineeTrainings_UserNotFound() {
        when(traineeRepository.existsByUserUserName(anyString())).thenReturn(false);

        assertThrows(UsernameNotFoundException.class,
                () -> traineeService.getTraineeTrainings("unknown", trainingRequest));
    }

    @Test
    void getTraineeTrainings_WithNullFilters() {
        TraineeTrainingRequest emptyRequest = TraineeTrainingRequest.builder().build();
        when(traineeRepository.existsByUserUserName(anyString())).thenReturn(true);
        when(trainingRepository.findTraineeTrainingsByCriteria(anyString(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<TrainingResponse> response = traineeService.getTraineeTrainings("john.doe", emptyRequest);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getTraineeTrainings_EmptyResult() {
        when(traineeRepository.existsByUserUserName(anyString())).thenReturn(true);
        when(trainingRepository.findTraineeTrainingsByCriteria(anyString(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<TrainingResponse> response = traineeService.getTraineeTrainings("john.doe", trainingRequest);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}