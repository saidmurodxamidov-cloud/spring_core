package org.example.service.impl;

import org.example.dto.request.TrainingTypeRequest;
import org.example.dto.response.TrainingTypeResponse;
import org.example.persistence.entity.TrainingTypeEntity;
import org.example.persistence.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceJpaTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceJpa trainingTypeService;

    private TrainingTypeRequest trainingTypeRequest;
    private TrainingTypeEntity trainingType;

    @BeforeEach
    void setUp() {
        trainingTypeRequest = new TrainingTypeRequest();
        trainingTypeRequest.setTrainingTypeName("Fitness");

        trainingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();
    }

    @Test
    void createTrainingType_VerifyEntityCreation() {
        when(trainingTypeRepository.save(any(TrainingTypeEntity.class))).thenAnswer(invocation -> {
            TrainingTypeEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        TrainingTypeResponse response = trainingTypeService.createTrainingType(trainingTypeRequest);

        verify(trainingTypeRepository, times(1)).save(argThat(entity ->
                entity.getTrainingTypeName().equals("Fitness")
        ));
        assertEquals("Fitness", response.getTrainingTypeName());
    }

    @Test
    void getAllTrainingTypes_Success() {
        TrainingTypeEntity yoga = TrainingTypeEntity.builder()
                .id(2L)
                .trainingTypeName("Yoga")
                .build();

        TrainingTypeEntity cardio = TrainingTypeEntity.builder()
                .id(3L)
                .trainingTypeName("Cardio")
                .build();

        when(trainingTypeRepository.findAll()).thenReturn(Arrays.asList(trainingType, yoga, cardio));

        List<TrainingTypeResponse> responses = trainingTypeService.getAllTrainingTypes();

        assertNotNull(responses);
        assertEquals(3, responses.size());
        assertEquals("Fitness", responses.get(0).getTrainingTypeName());
        assertEquals("Yoga", responses.get(1).getTrainingTypeName());
        assertEquals("Cardio", responses.get(2).getTrainingTypeName());
        verify(trainingTypeRepository, times(1)).findAll();
    }

    @Test
    void getAllTrainingTypes_EmptyList() {
        when(trainingTypeRepository.findAll()).thenReturn(Collections.emptyList());

        List<TrainingTypeResponse> responses = trainingTypeService.getAllTrainingTypes();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(trainingTypeRepository, times(1)).findAll();
    }

    @Test
    void getAllTrainingTypes_SingleType() {
        when(trainingTypeRepository.findAll()).thenReturn(Collections.singletonList(trainingType));

        List<TrainingTypeResponse> responses = trainingTypeService.getAllTrainingTypes();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Fitness", responses.get(0).getTrainingTypeName());
    }

    @Test
    void getAllTrainingTypes_VerifyMapping() {
        when(trainingTypeRepository.findAll()).thenReturn(Collections.singletonList(trainingType));

        List<TrainingTypeResponse> responses = trainingTypeService.getAllTrainingTypes();

        TrainingTypeResponse response = responses.get(0);
        assertEquals(trainingType.getId(), response.getId());
        assertEquals(trainingType.getTrainingTypeName(), response.getTrainingTypeName());
    }

    @Test
    void getAllTrainingTypes_OrderPreserved() {
        TrainingTypeEntity type1 = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("A-Type")
                .build();

        TrainingTypeEntity type2 = TrainingTypeEntity.builder()
                .id(2L)
                .trainingTypeName("B-Type")
                .build();

        TrainingTypeEntity type3 = TrainingTypeEntity.builder()
                .id(3L)
                .trainingTypeName("C-Type")
                .build();

        when(trainingTypeRepository.findAll()).thenReturn(Arrays.asList(type1, type2, type3));

        List<TrainingTypeResponse> responses = trainingTypeService.getAllTrainingTypes();

        assertEquals("A-Type", responses.get(0).getTrainingTypeName());
        assertEquals("B-Type", responses.get(1).getTrainingTypeName());
        assertEquals("C-Type", responses.get(2).getTrainingTypeName());
    }

    @Test
    void createTrainingType_WithLongName() {
        trainingTypeRequest.setTrainingTypeName("High Intensity Interval Training");
        TrainingTypeEntity hiitType = TrainingTypeEntity.builder()
                .id(4L)
                .trainingTypeName("High Intensity Interval Training")
                .build();

        when(trainingTypeRepository.save(any(TrainingTypeEntity.class))).thenReturn(hiitType);

        TrainingTypeResponse response = trainingTypeService.createTrainingType(trainingTypeRequest);

        assertNotNull(response);
        assertEquals("High Intensity Interval Training", response.getTrainingTypeName());
    }

    @Test
    void getAllTrainingTypes_LargeList() {
        List<TrainingTypeEntity> types = Arrays.asList(
                TrainingTypeEntity.builder().id(1L).trainingTypeName("Type1").build(),
                TrainingTypeEntity.builder().id(2L).trainingTypeName("Type2").build(),
                TrainingTypeEntity.builder().id(3L).trainingTypeName("Type3").build(),
                TrainingTypeEntity.builder().id(4L).trainingTypeName("Type4").build(),
                TrainingTypeEntity.builder().id(5L).trainingTypeName("Type5").build()
        );

        when(trainingTypeRepository.findAll()).thenReturn(types);

        List<TrainingTypeResponse> responses = trainingTypeService.getAllTrainingTypes();

        assertNotNull(responses);
        assertEquals(5, responses.size());
    }

    @Test
    void createTrainingType_SaveIsCalledOnce() {
        when(trainingTypeRepository.save(any(TrainingTypeEntity.class))).thenReturn(trainingType);

        trainingTypeService.createTrainingType(trainingTypeRequest);

        verify(trainingTypeRepository, times(1)).save(any(TrainingTypeEntity.class));
        verifyNoMoreInteractions(trainingTypeRepository);
    }

    @Test
    void getAllTrainingTypes_FindAllIsCalledOnce() {
        when(trainingTypeRepository.findAll()).thenReturn(Collections.singletonList(trainingType));

        trainingTypeService.getAllTrainingTypes();

        verify(trainingTypeRepository, times(1)).findAll();
        verifyNoMoreInteractions(trainingTypeRepository);
    }
}