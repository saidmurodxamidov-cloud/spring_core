package org.example.mapper;

import org.example.messageQueue.ActionType;
import org.example.dto.request.TrainerWorkloadRequest;
import org.example.persistence.entity.TrainingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerWorkloadMapper {

    @Mapping(target = "username", source = "training.trainer.user.userName")

    @Mapping(target = "firstName", source = "training.trainer.user.firstName")

    @Mapping(target = "lastName", source = "training.trainer.user.lastName")

    @Mapping(target = "active", source = "training.trainer.user.active")

    @Mapping(target = "trainingDate", source = "training.date")

    @Mapping(
            target = "duration",
            expression = "java((int) training.getTrainingDuration().toMinutes())"
    )

    @Mapping(
            target = "actionType",
            expression = "java(actionType)"
    )

    TrainerWorkloadRequest toDto(TrainingEntity training, ActionType actionType);
}

