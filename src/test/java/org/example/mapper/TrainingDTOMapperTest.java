package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.example.entity.TrainingEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.model.TrainingDTO;
import org.example.model.TrainingTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDTOMapperTest {

    private TrainingTypeMapper trainingTypeMapper;
    private TrainingMapper trainingMapper;
    private TrainingEntity trainingEntity;
    private TrainingDTO trainingDTO;

    @BeforeEach
    void setUp() {
        trainingMapper = Mappers.getMapper(TrainingMapper.class);
        trainingTypeMapper = Mappers.getMapper(TrainingTypeMapper.class);

        TraineeEntity trainee = TraineeEntity.builder()
                .id(10L)
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("123 Main Street")
                .build();

        TrainerEntity trainer = TrainerEntity.builder()
                .id(20L)
                .build();

        TrainingTypeEntity trainingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();

        trainingEntity = TrainingEntity.builder()
                .id(100L)
                .trainingName("Morning Yoga Session")
                .date(LocalDate.of(2024, 12, 1))
                .trainingDuration(Duration.ofHours(2))
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainingType)
                .build();

        trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingId(200L);
        trainingDTO.setTraineeId(15L);
        trainingDTO.setTrainerId(25L);
        trainingDTO.setDate(LocalDate.of(2024, 12, 5));
        trainingDTO.setTrainingName("Evening Fitness");
        trainingDTO.setTrainingType(new TrainingTypeDTO(2L, "Fitness"));
        trainingDTO.setTrainingDuration(Duration.ofMinutes(90));
    }

    @Test
    void testToEntityWithNullTraining() {
        assertNull(trainingMapper.toEntity(null));
    }
    @Test
    void testToTrainingWithNullEntity() {
        assertNull(trainingMapper.toTraining(null));
    }
    
    @Test
    void testToTrainingModelsWithEmptySet() {
        Set<TrainingEntity> entities = new HashSet<>();

        Set<TrainingDTO> dtos = trainingMapper.toTrainingModels(entities);

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void testToTrainingModelsWithNull() {
        assertNull(trainingMapper.toTrainingModels(null));
    }


}
