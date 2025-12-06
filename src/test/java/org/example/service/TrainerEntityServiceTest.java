package org.example.service;

import org.example.entity.TrainerEntity;
import org.example.entity.UserEntity;
import org.example.mapper.TrainerMapper;
import org.example.model.TrainerDTO;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.repository.UserRepository;
import org.example.util.UsernameGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerEntityServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @Mock
    private TrainingService trainingService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerEntityService service;

    private TrainerDTO trainerDTO;
    private TrainerEntity trainerEntity;

    @BeforeEach
    void setup() {
        trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName("John");
        trainerDTO.setLastName("Doe");
        trainerDTO.setPassword("pass".toCharArray());

        UserEntity user = new UserEntity();
        user.setPassword("encodedPass".toCharArray());

        trainerEntity = new TrainerEntity();
        trainerEntity.setUser(user);
    }

    @Test
    void createTrainer_success() {

        // Arrange
        TrainerDTO dto = new TrainerDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("rawPass".toCharArray());

        Set<String> existingUsernames = Set.of("john1", "john2");
        when(userRepository.findAllUserNames()).thenReturn(existingUsernames);

        when(bcrypt.encode("rawPass")).thenReturn("encodedPass");

        TrainerEntity trainerEntity = new TrainerEntity();
        when(trainerMapper.toEntity(any(TrainerDTO.class))).thenReturn(trainerEntity);

        // Mock static method for UsernameGenerator
        try (MockedStatic<UsernameGenerator> usernameMock = mockStatic(UsernameGenerator.class)) {
            usernameMock.when(() ->
                    UsernameGenerator.generateUsername(eq("John"), eq("Doe"), anySet())
            ).thenReturn("johnDoe3");

            // Act
            TrainerDTO result = service.createTrainer(dto);

            // Assert
            assertEquals("johnDoe3", result.getUserName());
            assertEquals("encodedPass", String.valueOf(result.getPassword()));

            verify(trainerRepository).save(trainerEntity);
        }
    }
    @Test
    void testAuthenticateSuccess() {
        UserEntity user = new UserEntity();
        user.setPassword("HASHED".toCharArray());
        trainerEntity.setUser(user);

        when(trainerRepository.findByUserUserName("john")).thenReturn(Optional.of(trainerEntity));
        when(bcrypt.matches("rawpass", "HASHED")).thenReturn(true);

        boolean result = service.authenticate("john", "rawpass");

        Assertions.assertTrue(result);
    }

    @Test
    void testAuthenticateWrongPassword() {
        UserEntity user = new UserEntity();
        user.setPassword("HASHED".toCharArray());
        trainerEntity.setUser(user);

        when(trainerRepository.findByUserUserName("john")).thenReturn(Optional.of(trainerEntity));
        when(bcrypt.matches("wrong", "HASHED")).thenReturn(false);

        boolean result = service.authenticate("john", "wrong");
        Assertions.assertFalse(result);
    }

    @Test
    void testAuthenticateTrainerNotFound() {
        when(trainerRepository.findByUserUserName("unknown")).thenReturn(Optional.empty());

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> service.authenticate("unknown", "pass")
        );
    }

    // -----------------------------------------
    // getTrainerByUsername() tests
    // -----------------------------------------
    @Test
    void testGetTrainerByUsernameSuccess() {
        TrainerDTO expectedDTO = new TrainerDTO();
        when(trainerRepository.findByUserUserName("john")).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toDTO(trainerEntity)).thenReturn(expectedDTO);

        TrainerDTO result = service.getTrainerByUsername("john");

        Assertions.assertNotNull(result);
        Assertions.assertSame(expectedDTO, result);
    }

    @Test
    void testGetTrainerByUsernameNotFound() {
        when(trainerRepository.findByUserUserName("john")).thenReturn(Optional.empty());

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> service.getTrainerByUsername("john")
        );
    }

}
