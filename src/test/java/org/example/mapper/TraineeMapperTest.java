package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.model.TraineeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {

    private TraineeMapper traineeMapper;
    private TraineeEntity traineeEntity;
    private TraineeDTO trainee;

    @BeforeEach
    void setUp() {
        // Initialize mapper with its dependencies
        traineeMapper = Mappers.getMapper(TraineeMapper.class);

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Williams")
                .userName("alice.williams")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        traineeEntity = TraineeEntity.builder()
                .id(10L)
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("123 Main Street")
                .user(userEntity)
                .build();

        trainee = new TraineeDTO();
        trainee.setUserId(2L);
        trainee.setFirstName("Bob");
        trainee.setLastName("Brown");
        trainee.setUserName("bob.brown");
        trainee.setPassword("secret456".toCharArray());
        trainee.setActive(false);
        trainee.setDateOfBirth(LocalDate.of(1990, 10, 20));
        trainee.setAddress("456 Oak Avenue");
    }

    @Test
    void testToDtoWithAllFields() {
        // When
        TraineeDTO dto = traineeMapper.toDTO(traineeEntity);

        // Then
        assertNotNull(dto);
        assertEquals("Alice", dto.getFirstName());
        assertEquals("Williams", dto.getLastName());
        assertEquals("alice.williams", dto.getUserName());
        assertArrayEquals("password123".toCharArray(), dto.getPassword());
        assertTrue(dto.isActive());
        assertEquals(LocalDate.of(1995, 5, 15), dto.getDateOfBirth());
        assertEquals("123 Main Street", dto.getAddress());
    }

    @Test
    void testToDtoWithNullEntity() {
        // When
        TraineeDTO dto = traineeMapper.toDTO(null);

        // Then
        assertNull(dto);
    }

    @Test
    void testToDtoWithNullUser() {
        // Given
        traineeEntity.setUser(null);

        // When
        TraineeDTO dto = traineeMapper.toDTO(traineeEntity);

        // Then
        assertNotNull(dto);
        assertNull(dto.getUserId());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertEquals(LocalDate.of(1995, 5, 15), dto.getDateOfBirth());
        assertEquals("123 Main Street", dto.getAddress());
    }

    @Test
    void testToEntityWithAllFields() {
        // When
        TraineeEntity entity = traineeMapper.toEntity(trainee);

        // Then
        assertNotNull(entity);
        assertEquals(LocalDate.of(1990, 10, 20), entity.getDateOfBirth());
        assertEquals("456 Oak Avenue", entity.getAddress());
    }

    @Test
    void testToEntityWithNullTrainee() {
        // When
        TraineeEntity entity = traineeMapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    @Test
    void testToEntityWithNullDateOfBirth() {
        // Given
        trainee.setDateOfBirth(null);

        // When
        TraineeEntity entity = traineeMapper.toEntity(trainee);

        // Then
        assertNotNull(entity);
        assertNull(entity.getDateOfBirth());
        assertEquals("456 Oak Avenue", entity.getAddress());
    }

    @Test
    void testToEntityWithNullAddress() {
        // Given
        trainee.setAddress(null);

        // When
        TraineeEntity entity = traineeMapper.toEntity(trainee);

        // Then
        assertNotNull(entity);
        assertEquals(LocalDate.of(1990, 10, 20), entity.getDateOfBirth());
        assertNull(entity.getAddress());
    }
}