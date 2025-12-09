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
    @Test
    void testUpdateEntityWithAllFields() {
        // Given - entity with existing values
        UserEntity existingUser = UserEntity.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Williams")
                .userName("alice.williams")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        TraineeEntity existingEntity = TraineeEntity.builder()
                .id(10L)
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("123 Main Street")
                .user(existingUser)
                .build();

        // DTO with new values
        TraineeDTO updateDTO = new TraineeDTO();
        updateDTO.setDateOfBirth(LocalDate.of(1990, 10, 20));
        updateDTO.setAddress("456 Oak Avenue");

        // When
        traineeMapper.updateEntity(updateDTO, existingEntity);

        // Then - only updatable fields should change
        assertEquals(10L, existingEntity.getId()); // id should remain unchanged
        assertEquals(LocalDate.of(1990, 10, 20), existingEntity.getDateOfBirth());
        assertEquals("456 Oak Avenue", existingEntity.getAddress());
        assertNotNull(existingEntity.getUser()); // user should remain unchanged
        assertEquals("Alice", existingEntity.getUser().getFirstName());
    }

    @Test
    void testUpdateEntityWithNullValues() {
        // Given - entity with existing values
        UserEntity existingUser = UserEntity.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Williams")
                .userName("alice.williams")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        TraineeEntity existingEntity = TraineeEntity.builder()
                .id(10L)
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("123 Main Street")
                .user(existingUser)
                .build();

        // DTO with null values - should NOT update due to NullValuePropertyMappingStrategy.IGNORE
        TraineeDTO updateDTO = new TraineeDTO();
        updateDTO.setDateOfBirth(null);
        updateDTO.setAddress(null);

        // When
        traineeMapper.updateEntity(updateDTO, existingEntity);

        // Then - existing values should remain unchanged due to IGNORE strategy
        assertEquals(LocalDate.of(1995, 5, 15), existingEntity.getDateOfBirth());
        assertEquals("123 Main Street", existingEntity.getAddress());
        assertNotNull(existingEntity.getUser());
    }

    @Test
    void testUpdateEntityPartialUpdate() {
        // Given - entity with existing values
        UserEntity existingUser = UserEntity.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Williams")
                .userName("alice.williams")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        TraineeEntity existingEntity = TraineeEntity.builder()
                .id(10L)
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("123 Main Street")
                .user(existingUser)
                .build();

        TraineeDTO updateDTO = new TraineeDTO();
        updateDTO.setAddress("789 Pine Road");

        traineeMapper.updateEntity(updateDTO, existingEntity);

        // Then
        assertEquals(LocalDate.of(1995, 5, 15), existingEntity.getDateOfBirth()); // unchanged
        assertEquals("789 Pine Road", existingEntity.getAddress()); // updated
    }

    @Test
    void testUpdateEntityIdAndUserNotAffected() {
        // Given
        UserEntity existingUser = UserEntity.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Williams")
                .userName("alice.williams")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        TraineeEntity existingEntity = TraineeEntity.builder()
                .id(10L)
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("123 Main Street")
                .user(existingUser)
                .build();

        // DTO trying to update with different user data
        TraineeDTO updateDTO = new TraineeDTO();
        updateDTO.setUserId(999L);
        updateDTO.setFirstName("NewName");
        updateDTO.setLastName("NewLastName");
        updateDTO.setUserName("newusername");
        updateDTO.setPassword("newpassword".toCharArray());
        updateDTO.setActive(false);
        updateDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));
        updateDTO.setAddress("New Address");

        // When
        traineeMapper.updateEntity(updateDTO, existingEntity);

        // Then - id and user should remain unchanged
        assertEquals(10L, existingEntity.getId());
        assertNotNull(existingEntity.getUser());
        assertEquals(1L, existingEntity.getUser().getId());
        assertEquals("Alice", existingEntity.getUser().getFirstName());
        assertEquals("Williams", existingEntity.getUser().getLastName());
        assertEquals("alice.williams", existingEntity.getUser().getUserName());

        // But address and dateOfBirth should be updated
        assertEquals(LocalDate.of(2000, 1, 1), existingEntity.getDateOfBirth());
        assertEquals("New Address", existingEntity.getAddress());
    }

    @Test
    void testUpdateEntityWithNullDTO() {
        // Given
        TraineeEntity existingEntity = TraineeEntity.builder()
                .id(10L)
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("123 Main Street")
                .build();

        // When
        traineeMapper.updateEntity(null, existingEntity);

        assertEquals(10L, existingEntity.getId());
        assertEquals(LocalDate.of(1995, 5, 15), existingEntity.getDateOfBirth());
        assertEquals("123 Main Street", existingEntity.getAddress());
    }
}