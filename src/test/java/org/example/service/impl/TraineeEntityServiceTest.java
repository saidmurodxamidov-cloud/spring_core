package org.example.service.impl;

import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.example.entity.UserEntity;
import org.example.mapper.TraineeMapper;
import org.example.model.TraineeDTO;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.UserRepository;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeEntityServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @InjectMocks
    private TraineeEntityService service;

    private TraineeEntity trainee;
    private TrainerEntity trainer1;
    private TrainerEntity trainer2;

    @BeforeEach
    void setUp() {
        trainee = new TraineeEntity();
        trainee.setTrainers(new HashSet<>());

        trainer1 = new TrainerEntity();
        trainer2 = new TrainerEntity();
    }

    // ------------------ updateTrainersList tests ------------------
    @Test
    void updateTrainersList_traineeNotFound_throwsException() {
        when(traineeRepository.findByUserUserName("john")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () ->
                service.updateTrainersList("john", List.of("trainer1")));

        assertTrue(ex.getMessage().contains("does not exist"));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void updateTrainersList_validInput_updatesTrainersSuccessfully() {
        UserEntity trainerUser1 = new UserEntity();
        trainerUser1.setUserName("trainer1");
        trainer1.setUser(trainerUser1);
        trainer1.setId(1L);

        UserEntity trainerUser2 = new UserEntity();
        trainerUser2.setUserName("trainer2");
        trainer2.setUser(trainerUser2);
        trainer2.setId(2L);

        UserEntity traineeUser = new UserEntity();
        traineeUser.setUserName("john");
        trainee.setUser(traineeUser);
        trainee.setId(10L);

        List<String> trainerUsernames = List.of("trainer1", "trainer2");

        Set<TrainerEntity> trainersSet = new HashSet<>();
        trainersSet.add(trainer1);
        trainersSet.add(trainer2);

        when(traineeRepository.findByUserUserName("john")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserNameIn(anyList())).thenReturn(trainersSet);
        when(traineeRepository.save(any(TraineeEntity.class))).thenReturn(trainee);

        service.updateTrainersList("john", trainerUsernames);

        ArgumentCaptor<TraineeEntity> traineeCaptor = ArgumentCaptor.forClass(TraineeEntity.class);
        verify(traineeRepository).save(traineeCaptor.capture());

        TraineeEntity savedTrainee = traineeCaptor.getValue();
        assertEquals(2, savedTrainee.getTrainers().size());
        assertTrue(savedTrainee.getTrainers().contains(trainer1));
        assertTrue(savedTrainee.getTrainers().contains(trainer2));

        verify(traineeRepository).findByUserUserName("john");
        verify(trainerRepository).findByUserUserNameIn(anyList());
    }

    // ------------------ deleteByUsername tests ------------------
    @Test
    void deleteByUsername_success() {
        String username = "john";

        UserEntity user = new UserEntity();
        user.setUserName(username);

        TraineeEntity trainee = new TraineeEntity();
        trainee.setUser(user);

        when(traineeRepository.findByUserUserName(username))
                .thenReturn(Optional.of(trainee));

        service.deleteByUsername(username);

        verify(traineeRepository).delete(trainee);
        verify(traineeRepository).findByUserUserName(username);
    }

    @Test
    void deleteByUsername_userNotFound() {
        String username = "missingUser";

        when(traineeRepository.findByUserUserName(username))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.deleteByUsername(username)
        );

        verify(traineeRepository, never()).delete(any());
    }

    // ------------------ createTrainee test ------------------
    @Test
    void createTrainee_success() {
        TraineeDTO dto = new TraineeDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        Set<String> usernames = Set.of("jdoe1", "jdoe2");
        when(userRepository.findAllUserNames()).thenReturn(usernames);

        try (MockedStatic<UsernameGenerator> usernameGenMock = mockStatic(UsernameGenerator.class);
             MockedStatic<PasswordGenerator> passwordGenMock = mockStatic(PasswordGenerator.class)) {

            usernameGenMock.when(() ->
                    UsernameGenerator.generateUsername(eq("John"), eq("Doe"), anySet())
            ).thenReturn("jdoe3");

            passwordGenMock.when(PasswordGenerator::generatePassword)
                    .thenReturn("secret123".toCharArray());

            when(bcrypt.encode(anyString())).thenReturn("encodedPassword");

            TraineeEntity entity = new TraineeEntity();
            UserEntity user = new UserEntity();
            user.setUserName("jdoe3");
            entity.setUser(user);

            when(traineeMapper.toEntity(any(TraineeDTO.class))).thenReturn(entity);

            TraineeDTO result = service.createTrainee(dto);

            assertEquals("jdoe3", result.getUserName());
            assertEquals("encodedPassword", String.valueOf(result.getPassword()));

            verify(traineeRepository).save(entity);
        }
    }
    @Test
    void getTraineeByUsername_success() {
        TraineeEntity entity = new TraineeEntity();
        TraineeDTO dto = new TraineeDTO();

        when(traineeRepository.findByUserUserName("john"))
                .thenReturn(Optional.of(entity));
        when(traineeMapper.toDTO(entity)).thenReturn(dto);

        TraineeDTO result = service.getTraineeByUsername("john");
        assertEquals(dto, result);
    }

    @Test
    void getTraineeByUsername_notFound() {
        when(traineeRepository.findByUserUserName("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.getTraineeByUsername("john")
        );
    }

    // ------------------ setActiveStatus tests ------------------
    @Test
    void setActiveStatus_success() {
        String username = "john";

        UserEntity user = new UserEntity();
        user.setActive(false);
        user.setUserName(username);

        TraineeEntity trainee = new TraineeEntity();
        trainee.setUser(user);

        TraineeDTO dto = new TraineeDTO();
        dto.setUserName(username);

        when(traineeRepository.findByUserUserName(username))
                .thenReturn(Optional.of(trainee));
        when(traineeMapper.toDTO(trainee)).thenReturn(dto);

        TraineeDTO result = service.setActiveStatus(username, true);

        assertTrue(trainee.getUser().isActive());
        assertEquals(dto, result);

        verify(traineeRepository).save(trainee);
    }

    @Test
    void setActiveStatus_userNotFound() {
        when(traineeRepository.findByUserUserName("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.setActiveStatus("john", true)
        );
    }
}
