package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.model.TraineeDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TraineeMapper {

    @Mapping(source = "user", target = ".")
    @Mapping(source = "id", target = "userId")
    TraineeDTO toDTO(TraineeEntity entity);


    @Mapping(target = "user.passwordHash",ignore = true)
    @Mapping(target = "user", source = ".")
    TraineeEntity toEntity(TraineeDTO traineeDTO);

    default UserEntity map(TraineeDTO dto) {
        if (dto == null) {
            return null;
        }
        UserEntity user = new UserEntity();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUserName(dto.getUserName());
        user.setActive(dto.isActive());
        return user;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TraineeDTO model, @MappingTarget TraineeEntity entity);

}

