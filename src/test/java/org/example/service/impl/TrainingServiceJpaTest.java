package org.example.service.impl;

import io.micrometer.core.instrument.Timer;
import org.example.client.ActionType;
import org.example.client.WorkloadSenderService;
import org.example.dto.request.TrainerWorkloadRequest;
import org.example.dto.request.TrainingAddRequest;
import org.example.mapper.TrainerWorkloadMapper;
import org.example.mapper.TrainingMapper;
import org.example.metrics.MetricsService;
import org.example.persistence.entity.*;
import org.example.persistence.model.TrainingDTO;
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

    @Mock
    private MetricsService metricsService;

    @Mock
    private WorkloadSenderService workloadSenderService;

    @Mock
    private TrainerWorkloadMapper workloadMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingServiceJpa trainingService;

    private TrainingAddRequest trainingRequest;
    private TraineeEntity trainee;
    private TrainerEntity trainer;
    private TrainingTypeEntity trainingType;
    private TrainerWorkloadRequest workloadRequest;

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

        UserEntity traineeUser = UserEntity.builder()
                .userName("john.doe")
                .firstName("John")
                .lastName("Doe")
                .build();

        UserEntity trainerUser = UserEntity.builder()
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

        workloadRequest = TrainerWorkloadRequest.builder().build();
    }

    private void stubHappyPath() {
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(TrainingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(workloadMapper.toDto(any(TrainingEntity.class), eq(ActionType.ADD))).thenReturn(workloadRequest);
        when(trainingMapper.toTraining(any(TrainingEntity.class))).thenReturn(new TrainingDTO());
        when(metricsService.startTrainingCreationTimer()).thenReturn(mock(Timer.Sample.class));
    }

    @Test
    void addTraining_Success() {
        stubHappyPath();

        trainingService.addTraining(trainingRequest);

        verify(traineeRepository).findByUserUserName("john.doe");
        verify(trainerRepository).findByUserUserName("jane.smith");
        verify(trainingTypeRepository).findByTrainingTypeName("Fitness");
        verify(trainingRepository).save(any(TrainingEntity.class));
        verify(workloadSenderService).sendWorkload(any(TrainerWorkloadRequest.class));
        verify(metricsService).incrementTrainingCreated();
        verify(metricsService).recordTrainingCreationDuration(any());

        assertTrue(trainer.getTrainees().contains(trainee));
        assertTrue(trainee.getTrainers().contains(trainer));
    }

    @Test
    void addTraining_TraineeNotFound() {
        when(metricsService.startTrainingCreationTimer()).thenReturn(mock(Timer.Sample.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> trainingService.addTraining(trainingRequest));

        verify(trainingRepository, never()).save(any());
        verify(workloadSenderService, never()).sendWorkload(any());
    }

    @Test
    void addTraining_TrainerNotFound() {
        when(metricsService.startTrainingCreationTimer()).thenReturn(mock(Timer.Sample.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> trainingService.addTraining(trainingRequest));

        verify(trainingRepository, never()).save(any());
        verify(workloadSenderService, never()).sendWorkload(any());
    }

    @Test
    void addTraining_TrainingTypeNotFound() {
        when(metricsService.startTrainingCreationTimer()).thenReturn(mock(Timer.Sample.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.addTraining(trainingRequest));

        verify(trainingRepository, never()).save(any());
        verify(workloadSenderService, never()).sendWorkload(any());
    }

    @Test
    void addTraining_WithNullTrainingName() {
        trainingRequest.setTrainingName(null);
        stubHappyPath();

        trainingService.addTraining(trainingRequest);

        verify(trainingRepository).save(any());
    }

    @Test
    void addTraining_VerifyBidirectionalRelationships() {
        stubHappyPath();

        trainingService.addTraining(trainingRequest);

        assertEquals(1, trainer.getTrainees().size());
        assertEquals(1, trainee.getTrainers().size());
    }

    @Test
    void addTraining_VerifyTrainingEntityCreation() {
        stubHappyPath();

        trainingService.addTraining(trainingRequest);

        verify(trainingRepository).save(argThat(training ->
                training.getTrainingName().equals("Morning Workout") &&
                        training.getTrainee().equals(trainee) &&
                        training.getTrainer().equals(trainer) &&
                        training.getTrainingType().equals(trainingType)
        ));
    }

    @Test
    void addTraining_AddsToCollections() {
        stubHappyPath();

        trainingService.addTraining(trainingRequest);

        assertEquals(1, trainer.getTrainings().size());
        assertEquals(1, trainee.getTrainings().size());
    }

    @Test
    void addTraining_SetsIdempotencyKeyOnWorkloadRequest() {
        stubHappyPath();

        trainingService.addTraining(trainingRequest);

        verify(workloadSenderService).sendWorkload(argThat(req ->
                req.getIdempotencyKey() != null && !req.getIdempotencyKey().isBlank()
        ));
    }

    @Test
    void addTraining_RecordsMetricsEvenOnException() {
        Timer.Sample sample = mock(Timer.Sample.class);
        when(metricsService.startTrainingCreationTimer()).thenReturn(sample);
        when(traineeRepository.findByUserUserName(anyString())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(trainingRequest));

        verify(metricsService).recordTrainingCreationDuration(sample);
        verify(metricsService, never()).incrementTrainingCreated();
    }

    @Test
    void deleteTraining_TrainingNotFound_DoesNothing() {
        when(trainingRepository.existsById(anyLong())).thenReturn(false);

        trainingService.deleteTraining(1L);

        verify(trainingRepository, never()).delete(any());
        verify(workloadSenderService, never()).deleteWorkload(any());
    }

    @Test
    void deleteTraining_Success() {
        TrainingEntity trainingEntity = new TrainingEntity();
        when(trainingRepository.existsById(1L)).thenReturn(true);
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(trainingEntity));
        when(workloadMapper.toDto(eq(trainingEntity), eq(ActionType.DELETE))).thenReturn(workloadRequest);

        trainingService.deleteTraining(1L);

        verify(trainingRepository).delete(trainingEntity);
        verify(workloadSenderService).deleteWorkload(workloadRequest);
    }
}