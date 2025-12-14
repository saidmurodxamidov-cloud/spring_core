package org.example.mapper;

import org.example.entity.TrainerEntity;
import org.example.entity.UserEntity;
import org.example.model.TrainerDTO;
import org.example.model.UserDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface TrainerMapper {

    @Mapping(source = "specializations",target = "specialization")
    @Mapping(source = "user", target = ".")
    @Mapping(source = "id", target = "userId")
    TrainerDTO toDTO(TrainerEntity entity);

    @InheritInverseConfiguration
    @Mapping(source = ".",target = "user")
    TrainerEntity toEntity(TrainerDTO trainerDTO);

    default UserEntity map(TrainerDTO dto){
        if(dto == null)
            return null;
        return UserEntity.builder()

                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .isActive(dto.isActive())
                .userName(dto.getUserName())
                .password(dto.getPassword())
                .build();

    }
    @Mapping(source = "specialization", target = "specializations")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user.userName", ignore = true)
    @Mapping(target = "user.password", ignore = true)
    void updateFromDTO(TrainerDTO trainerDTO, @MappingTarget TrainerEntity entity);
}
