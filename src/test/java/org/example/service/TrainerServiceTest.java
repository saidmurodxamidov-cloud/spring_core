package org.example.service;

import org.example.dao.TrainerDAO;
import org.example.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    private TrainerDAO trainerDAO;
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerDAO = mock(TrainerDAO.class);
        trainerService = new TrainerService(trainerDAO);
    }

    @Test
    void createTrainer_ShouldGenerateUsernameAndPassword() {
        when(trainerDAO.findAll()).thenReturn(List.of());

        Trainer trainer = new Trainer();
        trainer.setUserId(1L);
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setSpecialization("Yoga");

        trainerService.createTrainer(trainer);

        assertEquals("alice.smith", trainer.getUserName());
        assertNotNull(trainer.getPassword());
        assertEquals(8, trainer.getPassword().length());

        verify(trainerDAO).create(trainer);
    }

    @Test
    void updateTrainer_ShouldInvokeDAO() {
        Trainer trainer = new Trainer();
        trainer.setUserId(2L);
        trainerService.updateTrainer(trainer);
        verify(trainerDAO).update(trainer);
    }
}
