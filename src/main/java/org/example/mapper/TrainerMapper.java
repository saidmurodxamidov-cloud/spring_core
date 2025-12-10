package org.example.mapper;

import org.example.entity.TrainerEntity;
import org.example.model.TrainerDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface TrainerMapper {

    @Mapping(source = "specializations",target = "specialization")
    TrainerDTO toDTO(TrainerEntity entity);

    @InheritInverseConfiguration
    TrainerEntity toEntity(TrainerDTO trainerDTO);

    @Mapping(source = "specialization", target = "specializations")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user.userName", ignore = true)
    @Mapping(target = "user.password", ignore = true)
    void updateFromDTO(TrainerDTO trainerDTO, @MappingTarget TrainerEntity entity);
}
