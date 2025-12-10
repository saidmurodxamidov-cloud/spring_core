package org.example.mapper;

import org.example.entity.UserEntity;

import org.example.model.UserDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testToDto() {
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .userName("jdoe")
                .password("pass".toCharArray())
                .isActive(true)
                .build();
        UserDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getUserId());
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertArrayEquals(entity.getPassword(), dto.getPassword());
    }

    @Test
    void testToEntity() {
        UserDTO dto = new UserDTO(2L, "Jane", "Smith", "jsmith", "p2".toCharArray(), false);

        UserEntity entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getUserId(), entity.getId());
        assertEquals(dto.getLastName(), entity.getLastName());
        assertArrayEquals(dto.getPassword(), entity.getPassword());
    }
}