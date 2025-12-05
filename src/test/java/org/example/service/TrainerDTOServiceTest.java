package org.example.service;

import org.example.dao.TrainerDAO;
<<<<<<< HEAD:src/test/java/org/example/service/TrainerServiceTest.java
import org.example.model.Trainer;
=======
import org.example.model.TrainerDTO;
>>>>>>> main:src/test/java/org/example/service/TrainerDTOServiceTest.java
import org.example.model.TrainingTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDTOServiceTest {

    @Mock
    private TrainerDAO trainerDAO;
    @InjectMocks
    private TrainerService trainerService;
    private TrainerDTO trainerDTO;

    @BeforeEach
    void setUp() {
<<<<<<< HEAD:src/test/java/org/example/service/TrainerServiceTest.java
        trainer = new Trainer();
        trainer.setUserId(1L);
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setSpecialization(Set.of(new TrainingTypeDTO(1L,"YOGA")));
=======
        trainerDTO = new TrainerDTO();
        trainerDTO.setUserId(1L);
        trainerDTO.setFirstName("Alice");
        trainerDTO.setLastName("Smith");
        trainerDTO.setSpecialization(Set.of(new TrainingTypeDTO(1L,"YOGA")));
>>>>>>> main:src/test/java/org/example/service/TrainerDTOServiceTest.java
    }

    @Test
    void updateTrainer_ShouldInvokeDAO() {
        trainerService.updateTrainer(trainerDTO);
        verify(trainerDAO).update(trainerDTO);
    }


    @Test
    void getTrainerById_ShouldReturnTrainerFromDAO() {
        when(trainerDAO.findById(1L)).thenReturn(trainerDTO);

        TrainerDTO result = trainerService.getTrainerById(1L);

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        verify(trainerDAO).findById(1L);
    }

    @Test
    void getAllTrainers_ShouldReturnListFromDAO() {
        when(trainerDAO.findAll()).thenReturn(List.of(trainerDTO));

        List<TrainerDTO> result = trainerService.getAllTrainers();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(trainerDAO).findAll();
    }
}
