package org.example.service.impl;

import org.example.exception.EntityNotFoundException;
import org.example.mapper.TrainingMapper;
import org.example.persistence.model.TrainingDTO;
import org.example.persistence.model.TrainingTypeDTO;
import org.example.persistence.entity.TraineeEntity;
import org.example.persistence.entity.TrainerEntity;
import org.example.persistence.entity.TrainingEntity;
import org.example.persistence.entity.TrainingTypeEntity;
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

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceJpaTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingServiceJpa service;



    private TrainingDTO trainingDTO;

    @BeforeEach
    void setup() {
        trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingName("Morning Workout");
        trainingDTO.setTraineeId(10L);
        trainingDTO.setTrainerId(5L);
        trainingDTO.setTrainingDuration(Duration.ofMinutes(60));
        trainingDTO.setDate(LocalDate.now());

        TrainingTypeDTO typeDTO = new TrainingTypeDTO();
        typeDTO.setTrainingTypeName("Cardio");
        trainingDTO.setTrainingType(typeDTO);
    }

    @Test
    void createTraining_success() {
        // Arrange
        TraineeEntity trainee = new TraineeEntity();
        trainee.setId(10L);

        TrainerEntity trainer = new TrainerEntity();
        trainer.setId(5L);

        TrainingTypeEntity type = new TrainingTypeEntity();
        type.setTrainingTypeName("Cardio");

        // DTO to pass to service
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeId(10L);
        trainingDTO.setTrainerId(5L);
        trainingDTO.setTrainingName("Morning Workout");
        TrainingTypeDTO dto = new TrainingTypeDTO();
        dto.setTrainingTypeName("Cardio");
        trainingDTO.setTrainingType(dto);
        trainingDTO.setTrainingDuration(Duration.ofMinutes(60));

        // Mock repository calls
        when(traineeRepository.findById(10L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(5L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("Cardio"))
                .thenReturn(Optional.of(type));

        // Mock save
        when(trainingRepository.save(any(TrainingEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Mock mapper
        TrainingDTO mappedDto = new TrainingDTO();
        mappedDto.setTrainingName("Morning Workout");
        mappedDto.setTrainingDuration(Duration.ofMinutes(60));

        mappedDto.setTrainingType(dto);

        when(trainingMapper.toTraining(any(TrainingEntity.class)))
                .thenReturn(mappedDto);

        // Act
        TrainingDTO result = service.createTraining(trainingDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Morning Workout", result.getTrainingName());
        verify(trainingRepository, times(1)).save(any(TrainingEntity.class));
        verify(trainingMapper).toTraining(any(TrainingEntity.class));
    }


    @Test
    void createTraining_throwsWhenTraineeNotFound() {
        when(traineeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.createTraining(trainingDTO));
    }

    @Test
    void createTraining_throwsWhenTrainerNotFound() {
        when(traineeRepository.findById(10L)).thenReturn(Optional.of(new TraineeEntity()));
        when(trainerRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.createTraining(trainingDTO));
    }

    @Test
    void createTraining_throwsWhenTrainingTypeNotFound() {
        when(traineeRepository.findById(10L)).thenReturn(Optional.of(new TraineeEntity()));
        when(trainerRepository.findById(5L)).thenReturn(Optional.of(new TrainerEntity()));
        when(trainingTypeRepository.findByTrainingTypeName("Cardio"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.createTraining(trainingDTO));
    }

    @Test
    void getAllTrainerTrainings_trainerNotFound() {
        when(trainerRepository.findByUserUserName("trainer1")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.getAllTrainerTrainings("trainer1"));
    }


    @Test
    void getAllTraineeTrainings_traineeNotFound() {
        when(traineeRepository.findByUserUserName("trainee1")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> service.getAllTraineeTrainings("trainee1"));
    }


    @Test
    void getTraineeTrainings_userNotFound() {
        when(traineeRepository.existsByUserUserName("trainee1")).thenReturn(false);
        assertThrows(UsernameNotFoundException.class,
                () -> service.getTraineeTrainings("trainee1", null, null, null, null));
    }
    @Test
    void getTrainerTrainings_success() {
        when(trainerRepository.existsByUserUserName("trainer1")).thenReturn(true);
        when(trainingRepository.findTrainerTrainingsByCriteria(eq("trainer1"), any(), any(), any()))
                .thenReturn(List.of(new TrainingEntity()));
        when(trainingMapper.toTraining(any(TrainingEntity.class))).thenAnswer(invocation -> {
            TrainingEntity entity = invocation.getArgument(0);
            TrainingDTO dto = new TrainingDTO();
            dto.setTrainingName(entity.getTrainingName());
            dto.setDate(entity.getDate());
            dto.setTrainingDuration(entity.getTrainingDuration());
            return dto;
        });

        List<TrainingDTO> result = service.getTrainerTrainings("trainer1", null, null, null);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
