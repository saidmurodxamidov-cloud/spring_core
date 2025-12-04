package org.example.mapper;

import org.example.entity.TrainerEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.entity.UserEntity;
import org.example.model.Trainer;
import org.example.model.TrainingTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {

    private TrainerMapper trainerMapper;
    private TrainerEntity trainerEntity;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        // Initialize mapper
        trainerMapper = Mappers.getMapper(TrainerMapper.class);

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Michael")
                .lastName("Jordan")
                .userName("michael.jordan")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        TrainingTypeEntity yogaType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();

        TrainingTypeEntity fitnessType = TrainingTypeEntity.builder()
                .id(2L)
                .trainingTypeName("Fitness")
                .build();

        Set<TrainingTypeEntity> specializations = new HashSet<>();
        specializations.add(yogaType);
        specializations.add(fitnessType);

        trainerEntity = TrainerEntity.builder()
                .id(20L)
                .user(userEntity)
                .specializations(specializations)
                .build();

        trainer = new Trainer();
        trainer.setUserId(3L);
        trainer.setFirstName("Sarah");
        trainer.setLastName("Connor");
        trainer.setUserName("sarah.connor");
        trainer.setPassword("secret789".toCharArray());
        trainer.setActive(false);

        Set<TrainingTypeDTO> trainerSpecializations = new HashSet<>();
        trainerSpecializations.add(new TrainingTypeDTO(3L, "Cardio"));
        trainerSpecializations.add(new TrainingTypeDTO(4L, "Strength"));
        trainer.setSpecialization(trainerSpecializations);
    }


    @Test
    void testToDtoWithNullEntity() {
        // When
        Trainer dto = trainerMapper.toDTO(null);

        // Then
        assertNull(dto);
    }

    @Test
    void testToDtoWithNullUser() {
        // Given
        trainerEntity.setUser(null);

        // When
        Trainer dto = trainerMapper.toDTO(trainerEntity);

        // Then
        assertNotNull(dto);
        assertNull(dto.getUserId());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNotNull(dto.getSpecialization());
    }

    @Test
    void testToDtoWithEmptySpecializations() {
        // Given
        trainerEntity.setSpecializations(new HashSet<>());

        // When
        Trainer dto = trainerMapper.toDTO(trainerEntity);

        // Then
        assertNotNull(dto);
        assertNotNull(dto.getSpecialization());
        assertTrue(dto.getSpecialization().isEmpty());
    }

    @Test
    void testToEntityWithAllFields() {
        // When
        TrainerEntity entity = trainerMapper.toEntity(trainer);

        // Then
        assertNotNull(entity);
        assertNotNull(entity.getSpecializations());
        assertEquals(2, entity.getSpecializations().size());
        assertTrue(entity.getSpecializations().stream()
                .anyMatch(s -> s.getTrainingTypeName().equals("Cardio")));
        assertTrue(entity.getSpecializations().stream()
                .anyMatch(s -> s.getTrainingTypeName().equals("Strength")));
    }

    @Test
    void testToEntityWithNullTrainer() {
        // When
        TrainerEntity entity = trainerMapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    @Test
    void testToEntityWithNullSpecializations() {
        // Given
        trainer.setSpecialization(null);

        // When
        TrainerEntity entity = trainerMapper.toEntity(trainer);

        // Then
        assertNotNull(entity);
        assertNull(entity.getSpecializations());
    }

    @Test
    void testRoundTripMapping() {
        // When
        Trainer dto = trainerMapper.toDTO(trainerEntity);
        TrainerEntity mappedBackEntity = trainerMapper.toEntity(dto);

        // Then
        assertNotNull(mappedBackEntity);
        assertNotNull(mappedBackEntity.getSpecializations());
        assertEquals(trainerEntity.getSpecializations().size(),
                mappedBackEntity.getSpecializations().size());
    }
}