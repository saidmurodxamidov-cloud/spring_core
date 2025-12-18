package org.example.service.impl;

import org.example.dto.request.TrainerRequest;
import org.example.dto.request.TrainerTrainingsRequest;
import org.example.dto.request.TrainerUpdateRequest;
import org.example.dto.response.*;
import org.example.persistence.entity.*;
import org.example.persistence.repository.TrainerRepository;
import org.example.persistence.repository.TrainingRepository;
import org.example.persistence.repository.TrainingTypeRepository;
import org.example.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceJpaTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @InjectMocks
    private TrainerServiceJpa trainerService;

    private TrainerRequest trainerRequest;
    private TrainerUpdateRequest updateRequest;
    private TrainerTrainingsRequest trainingsRequest;
    private TrainerEntity trainer;
    private UserEntity user;
    private TrainingTypeEntity trainingType;
    private TraineeEntity trainee;

    @BeforeEach
    void setUp() {
        trainerRequest = new TrainerRequest();
        trainerRequest.setFirstname("Jane");
        trainerRequest.setLastname("Smith");
        trainerRequest.setSpecializations(Arrays.asList("Fitness", "Yoga"));

        updateRequest = TrainerUpdateRequest.builder()
                .firstname("Jane")
                .lastname("Smith")
                .isActive(true)
                .build();

        trainingsRequest = TrainerTrainingsRequest.builder()
                .from(LocalDate.of(2024, 1, 1))
                .to(LocalDate.of(2024, 12, 31))
                .traineeName("trainee1")
                .trainingType("Fitness")
                .build();

        user = UserEntity.builder()
                .userName("jane.smith")
                .passwordHash("encodedPassword")
                .firstName("Jane")
                .lastName("Smith")
                .isActive(true)
                .roles(new HashSet<>())
                .build();

        trainingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();

        trainer = TrainerEntity.builder()
                .user(user)
                .specializations(new HashSet<>(Arrays.asList(trainingType)))
                .trainees(new HashSet<>())
                .trainings(new HashSet<>())
                .build();

        trainee = TraineeEntity.builder()
                .user(UserEntity.builder()
                        .userName("john.doe")
                        .firstName("John")
                        .lastName("Doe")
                        .build())
                .build();
    }



    @Test
    void createTrainer_SpecializationNotFound() {
        when(userRepository.findAllUserNames()).thenReturn(new HashSet<>());
        when(bcrypt.encode(anyString())).thenReturn("encodedPassword");
        when(trainingTypeRepository.findByTrainingTypeNameIn(anyList())).thenReturn(new HashSet<>());

        assertThrows(IllegalArgumentException.class, () -> trainerService.createTrainer(trainerRequest));
        verify(trainerRepository, never()).save(any(TrainerEntity.class));
    }

    @Test
    void createTrainer_PartialSpecializationsFound() {
        Set<TrainingTypeEntity> partialSpecializations = new HashSet<>(Arrays.asList(trainingType));

        when(userRepository.findAllUserNames()).thenReturn(new HashSet<>());
        when(bcrypt.encode(anyString())).thenReturn("encodedPassword");
        when(trainingTypeRepository.findByTrainingTypeNameIn(anyList())).thenReturn(partialSpecializations);

        assertThrows(IllegalArgumentException.class, () -> trainerService.createTrainer(trainerRequest));
    }

    @Test
    void createTrainer_WithNullSpecializations() {
        trainerRequest.setSpecializations(null);

        when(userRepository.findAllUserNames()).thenReturn(new HashSet<>());
        when(bcrypt.encode(anyString())).thenReturn("encodedPassword");
        when(trainingTypeRepository.findByTrainingTypeNameIn(isNull())).thenReturn(new HashSet<>());
        when(trainerRepository.save(any(TrainerEntity.class))).thenReturn(trainer);

        AuthResponse response = trainerService.createTrainer(trainerRequest);

        assertNotNull(response);
        verify(trainerRepository, times(1)).save(any(TrainerEntity.class));
    }

    @Test
    void getTrainerProfile_Success() {
        trainer.setTrainees(new HashSet<>(Arrays.asList(trainee)));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));

        TrainerProfileResponse response = trainerService.getTrainerProfile("jane.smith");

        assertNotNull(response);
        assertEquals("Jane", response.getFirstname());
        assertEquals("Smith", response.getLastname());
        assertTrue(response.isActive());
        assertEquals(1, response.getTrainees().size());
        verify(trainerRepository, times(1)).findByUserUserName("jane.smith");
    }

    @Test
    void getTrainerProfile_NotFound() {
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> trainerService.getTrainerProfile("unknown"));
        verify(trainerRepository, times(1)).findByUserUserName("unknown");
    }

    @Test
    void updateTrainer_Success() {
        trainer.setTrainees(new HashSet<>(Arrays.asList(trainee)));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));

        TrainerUpdateResponse response = trainerService.updateTrainer("jane.smith", updateRequest);

        assertNotNull(response);
        assertEquals("jane.smith", response.getUsername());
        assertEquals("Jane", response.getFirstname());
        assertEquals("Smith", response.getLastname());
        assertTrue(response.isActive());
        verify(trainerRepository, times(1)).findByUserUserName("jane.smith");
    }

    @Test
    void updateTrainer_NotFound() {
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> trainerService.updateTrainer("unknown", updateRequest));
    }

    @Test
    void updateTrainer_ChangingActiveStatus() {
        updateRequest.setIsActive(false);
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));

        TrainerUpdateResponse response = trainerService.updateTrainer("jane.smith", updateRequest);

        assertNotNull(response);
        assertFalse(user.isActive());
    }

    @Test
    void getTrainerTrainings_Success() {
        TrainingEntity training = TrainingEntity.builder()
                .trainingName("Test Training")
                .date(LocalDate.now())
                .trainingType(trainingType)
                .trainingDuration(Duration.ofMinutes(60))
                .build();

        when(trainerRepository.existsByUserUserName(anyString())).thenReturn(true);
        when(trainingRepository.findTrainerTrainingsByCriteria(anyString(), any(), any(), any()))
                .thenReturn(Arrays.asList(training));

        List<TrainingResponse> response = trainerService.getTrainerTrainings("jane.smith", trainingsRequest);

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(trainerRepository, times(1)).existsByUserUserName("jane.smith");
        verify(trainingRepository, times(1))
                .findTrainerTrainingsByCriteria(anyString(), any(), any(), any());
    }

    @Test
    void getTrainerTrainings_UserNotFound() {
        when(trainerRepository.existsByUserUserName(anyString())).thenReturn(false);

        assertThrows(UsernameNotFoundException.class,
                () -> trainerService.getTrainerTrainings("unknown", trainingsRequest));
    }

    @Test
    void getTrainerTrainings_WithNullFilters() {
        TrainerTrainingsRequest emptyRequest = TrainerTrainingsRequest.builder().build();
        when(trainerRepository.existsByUserUserName(anyString())).thenReturn(true);
        when(trainingRepository.findTrainerTrainingsByCriteria(anyString(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<TrainingResponse> response = trainerService.getTrainerTrainings("jane.smith", emptyRequest);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getTrainerTrainings_EmptyResult() {
        when(trainerRepository.existsByUserUserName(anyString())).thenReturn(true);
        when(trainingRepository.findTrainerTrainingsByCriteria(anyString(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<TrainingResponse> response = trainerService.getTrainerTrainings("jane.smith", trainingsRequest);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getTrainerTrainings_WithMultipleTrainings() {
        TrainingEntity training1 = TrainingEntity.builder()
                .trainingName("Morning Session")
                .date(LocalDate.now())
                .trainingType(trainingType)
                .trainingDuration(Duration.ofMinutes(60))
                .build();
        TrainingEntity training2 = TrainingEntity.builder()
                .trainingName("Evening Session")
                .date(LocalDate.now())
                .trainingType(trainingType)
                .trainingDuration(Duration.ofMinutes(90))
                .build();

        when(trainerRepository.existsByUserUserName(anyString())).thenReturn(true);
        when(trainingRepository.findTrainerTrainingsByCriteria(anyString(), any(), any(), any()))
                .thenReturn(Arrays.asList(training1, training2));

        List<TrainingResponse> response = trainerService.getTrainerTrainings("jane.smith", trainingsRequest);

        assertNotNull(response);
        assertEquals(2, response.size());
    }
}