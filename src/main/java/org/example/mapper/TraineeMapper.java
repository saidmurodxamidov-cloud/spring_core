package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.model.TraineeDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TraineeMapper {

    @Mapping(source = "user", target = ".")
    TraineeDTO toDTO(TraineeEntity entity);


    TraineeEntity toEntity(TraineeDTO traineeDTO);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TraineeDTO model, @MappingTarget TraineeEntity entity);

}

