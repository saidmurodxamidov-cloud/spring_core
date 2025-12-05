package org.example.mapper;

import org.example.entity.TrainingTypeEntity;
import org.example.model.TrainingTypeDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeMapperTest {

    @Spy
    private TrainingTypeMapper mapper = Mappers.getMapper(TrainingTypeMapper.class);

    @Test
    void testToDTO() {
        TrainingTypeEntity entity = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("YOGA")
                .build();

        TrainingTypeDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getTrainingTypeName(), dto.getTrainingTypeName());
    }

    @Test
    void testToEntity() {
        TrainingTypeDTO dto = new TrainingTypeDTO(2L, "Pilates");

        TrainingTypeEntity entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getTrainingTypeName(), entity.getTrainingTypeName());
    }
}