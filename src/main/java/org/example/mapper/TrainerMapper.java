package org.example.mapper;

import org.example.entity.TrainerEntity;
import org.example.model.TrainerDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface TrainerMapper {

    @Mapping(source = "specializations",target = "specialization")
    TrainerDTO toDTO(TrainerEntity entity);

    @InheritInverseConfiguration
    TrainerEntity toEntity(TrainerDTO trainerDTO);
}
