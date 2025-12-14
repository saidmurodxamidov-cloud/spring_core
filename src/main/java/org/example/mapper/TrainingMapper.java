package org.example.mapper;

import org.example.entity.TrainingEntity;
import org.example.model.TrainingDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {TrainingTypeMapper.class})
public interface TrainingMapper {

    // Mapping from DTO -> Entity
    @Mapping(source = "trainingId", target = "id")
    @Mapping(source = "traineeId", target = "trainee.id")
    @Mapping(source = "trainerId", target = "trainer.id")
    @Mapping(source = "trainingType", target = "trainingType")
    TrainingEntity toEntity(TrainingDTO dto);

    // Mapping from Entity -> DTO
    @Mapping(source = "id", target = "trainingId")
    @Mapping(source = "trainee.id", target = "traineeId")
    @Mapping(source = "trainer.id", target = "trainerId")
    @Mapping(source = "trainingType", target = "trainingType")
    TrainingDTO toTraining(TrainingEntity entity);

    Set<TrainingDTO> toTrainingModels(Set<TrainingEntity> entities);
}

