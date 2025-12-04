package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.model.Trainee;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TraineeMapper {

    @Mapping(source = "user", target = ".")
    Trainee toDTO(TraineeEntity entity);


    TraineeEntity toEntity(Trainee trainee);
}
