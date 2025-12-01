package org.example.mapper;

import org.example.entity.TrainingTypeEntity;
import org.example.model.TrainingType;
import org.example.model.TrainingTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingTypeDTO toDTO(TrainingTypeEntity entity);


    TrainingTypeEntity toEntity(TrainingTypeDTO dto);
}
