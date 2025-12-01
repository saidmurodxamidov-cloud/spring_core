package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.model.Trainee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TraineeMapper {

    Trainee toDTO(TraineeEntity entity);

    TraineeEntity toEntity(Trainee trainee);
}
