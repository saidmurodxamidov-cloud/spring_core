package org.example.mapper;

import org.example.entity.TrainerEntity;
import org.example.model.Trainer;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface TrainerMapper {

    @Mapping(source = "specializations",target = "specialization")
    Trainer toDTO(TrainerEntity entity);

    @InheritInverseConfiguration
    TrainerEntity toEntity(Trainer trainer);
}
