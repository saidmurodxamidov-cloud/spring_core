package org.example.mapper;

import org.example.entity.UserEntity;
import org.example.model.UserDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id",target = "userId")
    UserDTO toDto(UserEntity entity);

    @InheritInverseConfiguration
    UserEntity toEntity(UserDTO userDTO);
}
