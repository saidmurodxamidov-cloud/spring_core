package org.example.service.dao;

import org.example.dao.TrainerDAO;
import org.example.model.Trainer;
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
class TrainerServiceTest {

    @Mock
    private TrainerDAO trainerDAO;
    @InjectMocks
    private TrainerService trainerService;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setUserId(1L);
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setSpecialization(Set.of(new TrainingTypeDTO(1L,"YOGA")));
    }

    @Test
    void updateTrainer_ShouldInvokeDAO() {
        trainerService.updateTrainer(trainer);
        verify(trainerDAO).update(trainer);
    }


    @Test
    void getTrainerById_ShouldReturnTrainerFromDAO() {
        when(trainerDAO.findById(1L)).thenReturn(trainer);

        Trainer result = trainerService.getTrainerById(1L);

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        verify(trainerDAO).findById(1L);
    }

    @Test
    void getAllTrainers_ShouldReturnListFromDAO() {
        when(trainerDAO.findAll()).thenReturn(List.of(trainer));

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(trainerDAO).findAll();
    }
}
