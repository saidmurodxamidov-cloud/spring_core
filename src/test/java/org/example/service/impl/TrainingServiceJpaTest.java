package org.example.service.impl;

import org.example.dto.request.TrainingAddRequest;
import org.example.persistence.entity.*;
import org.example.persistence.repository.TraineeRepository;
import org.example.persistence.repository.TrainerRepository;
import org.example.persistence.repository.TrainingRepository;
import org.example.persistence.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceJpaTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingServiceJpa trainingService;

    private TrainingAddRequest trainingRequest;
    private TraineeEntity trainee;
    private TrainerEntity trainer;
    private TrainingTypeEntity trainingType;
    private UserEntity traineeUser;
    private UserEntity trainerUser;

    @BeforeEach
    void setUp() {
        trainingRequest = TrainingAddRequest.builder()
                .trainingName("Morning Workout")
                .traineeUsername("john.doe")
                .trainerUsername("jane.smith")
                .trainingType("Fitness")
                .trainingDate(LocalDate.now())
                .trainingDurationInMinutes(60)
                .build();

        traineeUser = UserEntity.builder()
                .userName("john.doe")
                .firstName("John")
                .lastName("Doe")
                .build();

        trainerUser = UserEntity.builder()
                .userName("jane.smith")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        trainee = TraineeEntity.builder()
                .user(traineeUser)
                .trainers(new HashSet<>())
                .trainings(new HashSet<>())
                .build();

        trainer = TrainerEntity.builder()
                .user(trainerUser)
                .trainees(new HashSet<>())
                .trainings(new HashSet<>())
                .build();

        trainingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();
    }

    @Test
    void addTraining_Success() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenReturn(new TrainingEntity());

        trainingService.addTraining(trainingRequest);

        verify(traineeRepository, times(1)).findByUserUserName("john.doe");
        verify(trainerRepository, times(1)).findByUserUserName("jane.smith");
        verify(trainingTypeRepository, times(1)).findByTrainingTypeName("Fitness");
        verify(trainingRepository, times(1)).save(any(TrainingEntity.class));
        assertTrue(trainer.getTrainees().contains(trainee));
        assertTrue(trainee.getTrainers().contains(trainer));
    }

    @Test
    void addTraining_TraineeNotFound() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> trainingService.addTraining(trainingRequest));
        verify(traineeRepository, times(1)).findByUserUserName("john.doe");
        verify(trainingRepository, never()).save(any(TrainingEntity.class));
    }

    @Test
    void addTraining_TrainerNotFound() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> trainingService.addTraining(trainingRequest));
        verify(trainerRepository, times(1)).findByUserUserName("jane.smith");
        verify(trainingRepository, never()).save(any(TrainingEntity.class));
    }

    @Test
    void addTraining_TrainingTypeNotFound() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(trainingRequest));
        verify(trainingTypeRepository, times(1)).findByTrainingTypeName("Fitness");
        verify(trainingRepository, never()).save(any(TrainingEntity.class));
    }

    @Test
    void addTraining_WithNullTrainingName() {
        trainingRequest.setTrainingName(null);
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenReturn(new TrainingEntity());

        trainingService.addTraining(trainingRequest);

        verify(trainingRepository, times(1)).save(any(TrainingEntity.class));
    }

    @Test
    void addTraining_WithPastDate() {
        trainingRequest.setTrainingDate(LocalDate.of(2020, 1, 1));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenReturn(new TrainingEntity());

        trainingService.addTraining(trainingRequest);

        verify(trainingRepository, times(1)).save(any(TrainingEntity.class));
    }

    @Test
    void addTraining_WithZeroDuration() {
        trainingRequest.setTrainingDurationInMinutes(0);
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenReturn(new TrainingEntity());

        trainingService.addTraining(trainingRequest);

        verify(trainingRepository, times(1)).save(any(TrainingEntity.class));
    }

    @Test
    void addTraining_WithLongDuration() {
        trainingRequest.setTrainingDurationInMinutes(480);
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenReturn(new TrainingEntity());

        trainingService.addTraining(trainingRequest);

        verify(trainingRepository, times(1)).save(any(TrainingEntity.class));
    }

    @Test
    void addTraining_VerifyBidirectionalRelationships() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        trainingService.addTraining(trainingRequest);

        assertTrue(trainer.getTrainees().contains(trainee));
        assertTrue(trainee.getTrainers().contains(trainer));
        assertEquals(1, trainer.getTrainees().size());
        assertEquals(1, trainee.getTrainers().size());
    }

    @Test
    void addTraining_VerifyTrainingEntityCreation() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        trainingService.addTraining(trainingRequest);

        verify(trainingRepository, times(1)).save(argThat(training ->
                training.getTrainingName().equals("Morning Workout") &&
                        training.getTrainee().equals(trainee) &&
                        training.getTrainer().equals(trainer) &&
                        training.getTrainingType().equals(trainingType)
        ));
    }

    @Test
    void addTraining_AddsToCollections() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        trainingService.addTraining(trainingRequest);

        assertEquals(1, trainer.getTrainings().size());
        assertEquals(1, trainee.getTrainings().size());
    }

    @Test
    void addTraining_MultipleTrainings() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenReturn(new TrainingEntity());

        trainingService.addTraining(trainingRequest);
        trainingService.addTraining(trainingRequest);

        assertEquals(1, trainer.getTrainees().size());
        assertEquals(1, trainee.getTrainers().size());
        verify(trainingRepository, times(2)).save(any(TrainingEntity.class));
    }
}