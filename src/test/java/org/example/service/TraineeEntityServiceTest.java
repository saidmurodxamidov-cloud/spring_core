package org.example.service;

import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.example.entity.UserEntity;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeEntityServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TraineeEntityService service;

    private TraineeEntity trainee;
    private TrainerEntity trainer1;
    private TrainerEntity trainer2;

    @BeforeEach
    void setUp() {
        // Use real objects to avoid mocking final Lombok methods
        trainee = new TraineeEntity();
        trainee.setTrainers(new HashSet<>());

        trainer1 = new TrainerEntity();
        trainer2 = new TrainerEntity();
    }

  

    @Test
    void updateTrainersList_traineeNotFound_throwsException() {
        when(traineeRepository.findByUserUserName("john")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () ->
                service.updateTrainersList("john", List.of("trainer1")));

        assertTrue(ex.getMessage().contains("does not exist"));
        verify(traineeRepository, never()).save(any());
    }


    @Test
    void updateTrainersList_validInput_updatesTrainersSuccessfully() {
        // Arrange
        UserEntity trainerUser1 = new UserEntity();
        trainerUser1.setUserName("trainer1");
        trainer1.setUser(trainerUser1);
        trainer1.setId(1L);

        UserEntity trainerUser2 = new UserEntity();
        trainerUser2.setUserName("trainer2");
        trainer2.setUser(trainerUser2);
        trainer2.setId(2L);

        UserEntity traineeUser = new UserEntity();
        traineeUser.setUserName("john");
        trainee.setUser(traineeUser);
        trainee.setId(10L); // Also set trainee ID

        List<String> trainerUsernames = List.of("trainer1", "trainer2");

        // Create a HashSet with exactly 2 trainers
        Set<TrainerEntity> trainersSet = new HashSet<>();
        trainersSet.add(trainer1);
        trainersSet.add(trainer2);

        when(traineeRepository.findByUserUserName("john")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserNameIn(anyList())).thenReturn(trainersSet);
        when(traineeRepository.save(any(TraineeEntity.class))).thenReturn(trainee);

        // Act
        service.updateTrainersList("john", trainerUsernames);

        // Assert
        ArgumentCaptor<TraineeEntity> traineeCaptor = ArgumentCaptor.forClass(TraineeEntity.class);
        verify(traineeRepository).save(traineeCaptor.capture());

        TraineeEntity savedTrainee = traineeCaptor.getValue();
        assertEquals(2, savedTrainee.getTrainers().size());
        assertTrue(savedTrainee.getTrainers().contains(trainer1));
        assertTrue(savedTrainee.getTrainers().contains(trainer2));

        verify(traineeRepository).findByUserUserName("john");
        verify(trainerRepository).findByUserUserNameIn(anyList());
    }
}
