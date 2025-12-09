package org.example.service;

import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.mapper.TraineeMapper;
import org.example.repository.TraineeRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TraineeEntityServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @InjectMocks
    private TraineeEntityService service;

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
