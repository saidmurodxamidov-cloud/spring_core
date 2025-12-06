package org.example.service;

import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.mapper.TraineeMapper;
import org.example.model.TraineeDTO;
import org.example.repository.TraineeRepository;
import org.example.repository.UserRepository;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TraineeEntityServiceTest {

    private TraineeRepository traineeRepository;
    private TraineeMapper traineeMapper;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bcrypt;

    private TraineeEntityService service;

    @BeforeEach
    void setUp() {
        traineeRepository = mock(TraineeRepository.class);
        traineeMapper = mock(TraineeMapper.class);
        userRepository = mock(UserRepository.class);
        bcrypt = mock(BCryptPasswordEncoder.class);

        service = new TraineeEntityService(
                traineeRepository,
                traineeMapper,
                userRepository,
                bcrypt
        );
    }
    @Test
    void deleteByUsername_success() {
        // Arrange
        String username = "john";

        UserEntity user = new UserEntity();
        user.setUserName(username);

        TraineeEntity trainee = new TraineeEntity();
        trainee.setUser(user);

        when(traineeRepository.findByUserUserName(username))
                .thenReturn(Optional.of(trainee));

        // Act
        service.deleteByUsername(username);

        // Assert
        verify(traineeRepository).delete(trainee);
        verify(traineeRepository).findByUserUserName(username);
    }

    @Test
    void deleteByUsername_userNotFound() {
        // Arrange
        String username = "missingUser";

        when(traineeRepository.findByUserUserName(username))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> service.deleteByUsername(username)
        );

        verify(traineeRepository, never()).delete(any());
    }


}