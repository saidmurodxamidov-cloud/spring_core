package org.example.service;

import org.example.entity.*;
import org.example.exception.EntityNotFoundException;
import org.example.model.TrainingDTO;
import org.example.model.TrainingTypeDTO;
import org.example.repository.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingEntityServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingEntityService service;

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
        // Mock entities returned by repositories
        TraineeEntity trainee = new TraineeEntity();
        trainee.setId(10L);

        TrainerEntity trainer = new TrainerEntity();
        trainer.setId(5L);

        TrainingTypeEntity type = new TrainingTypeEntity();
        type.setTrainingTypeName("Cardio");

        // Mock repository calls
        when(traineeRepository.findById(10L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(5L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("Cardio"))
                .thenReturn(Optional.of(type));

        // Mock save behavior
        when(trainingRepository.save(any(TrainingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Execute
        TrainingDTO result = service.createTraining(trainingDTO);

        // Verify
        assertEquals("Morning Workout", result.getTrainingName());
        verify(trainingRepository, times(1)).save(any(TrainingEntity.class));
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
}
