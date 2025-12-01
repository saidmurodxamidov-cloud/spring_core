package org.example.mapper;

import org.example.entity.UserEntity;
import org.example.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id",target = "userId")
    User toDto(UserEntity entity);

    @InheritInverseConfiguration
    UserEntity toEntity(User user);
}
