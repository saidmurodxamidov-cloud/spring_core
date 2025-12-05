package org.example.mapper;

import org.example.entity.TrainingEntity;
import org.example.model.TrainingDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = TrainingTypeMapper.class)
public interface TrainingMapper {
    @Mapping(source = "trainingId",target = "id")
    @Mapping(source = "trainerId", target = "trainer.id")
    @Mapping(source = "traineeId", target = "trainee.id")
    TrainingEntity toEntity(TrainingDTO trainingDTO);

    @InheritInverseConfiguration
    TrainingDTO toTraining(TrainingEntity entity);

    Set<TrainingDTO> toTrainingModels(Set<TrainingEntity> entities);
}
