package org.example.mapper;

import org.example.persistence.entity.TrainingTypeEntity;
import org.example.persistence.model.TrainingTypeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingTypeDTO toDTO(TrainingTypeEntity entity);


    TrainingTypeEntity toEntity(TrainingTypeDTO dto);
}
