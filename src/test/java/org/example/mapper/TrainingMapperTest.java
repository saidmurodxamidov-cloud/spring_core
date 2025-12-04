package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.example.entity.TrainingEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.model.Training;
import org.example.model.TrainingTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {

    private TrainingTypeMapper trainingTypeMapper;
    private TrainingMapper trainingMapper;
    private TrainingEntity trainingEntity;
    private Training training;

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

        training = new Training();
        training.setTrainingId(200L);
        training.setTraineeId(15L);
        training.setTrainerId(25L);
        training.setDate(LocalDate.of(2024, 12, 5));
        training.setTrainingName("Evening Fitness");
        training.setTrainingType(new TrainingTypeDTO(2L, "Fitness"));
        training.setTrainingDuration(Duration.ofMinutes(90));
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

        Set<Training> dtos = trainingMapper.toTrainingModels(entities);

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void testToTrainingModelsWithNull() {
        assertNull(trainingMapper.toTrainingModels(null));
    }


}
