package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.model.TraineeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TraineeMapper {

    @Mapping(source = "user", target = ".")
    TraineeDTO toDTO(TraineeEntity entity);


    TraineeEntity toEntity(TraineeDTO traineeDTO);
}
