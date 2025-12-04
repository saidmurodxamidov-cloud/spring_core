package org.example.service;

import org.example.entity.TrainingTypeEntity;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.TrainingTypeMapper;
import org.example.model.TrainingTypeDTO;
import org.example.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTrainingTypeByName_Found() {
        String name = "Yoga";
        TrainingTypeEntity entity = new TrainingTypeEntity();
        entity.setId(1L);
        entity.setTrainingTypeName(name);

        TrainingTypeDTO dto = new TrainingTypeDTO();
        dto.setId(1L);
        dto.setTrainingTypeName(name);

        when(trainingTypeRepository.findByTrainingTypeName(name))
                .thenReturn(Optional.of(entity));

        when(trainingTypeMapper.toDTO(entity)).thenReturn(dto);

        TrainingTypeDTO result = trainingTypeService.getTrainingTypeByName(name);

        assertNotNull(result);
        assertEquals(name, result.getTrainingTypeName());
        verify(trainingTypeRepository, times(1)).findByTrainingTypeName(name);
        verify(trainingTypeMapper, times(1)).toDTO(entity);
    }

    @Test
    void testGetTrainingTypeByName_NotFound() {
        String name = "UnknownType";

        when(trainingTypeRepository.findByTrainingTypeName(name))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            trainingTypeService.getTrainingTypeByName(name);
        });

        verify(trainingTypeRepository, times(1)).findByTrainingTypeName(name);
        verifyNoInteractions(trainingTypeMapper); // mapper should never be called
    }
}
