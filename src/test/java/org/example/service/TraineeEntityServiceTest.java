package org.example.service;

import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.mapper.TraineeMapper;
import org.example.model.TraineeDTO;
import org.example.repository.TraineeRepository;
import org.example.repository.UserRepository;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private TraineeMapper traineeMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @InjectMocks
    private TraineeEntityService service;

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

    // ------------------ authenticate tests ------------------
    @Test
    void authenticate_success() {
        UserEntity user = new UserEntity();
        user.setActive(true);
        user.setPassword("encoded".toCharArray());

        TraineeEntity trainee = new TraineeEntity();
        trainee.setUser(user);

        when(traineeRepository.findByUserUserName("john"))
                .thenReturn(Optional.of(trainee));
        when(bcrypt.matches("123", "encoded")).thenReturn(true);

        boolean result = service.authenticate("john", "123");
        assertTrue(result);
    }

    @Test
    void authenticate_userNotFound() {
        when(traineeRepository.findByUserUserName("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.authenticate("john", "pass")
        );
    }

    @Test
    void authenticate_notActive() {
        UserEntity user = new UserEntity();
        user.setActive(false);

        TraineeEntity trainee = new TraineeEntity();
        trainee.setUser(user);

        when(traineeRepository.findByUserUserName("john"))
                .thenReturn(Optional.of(trainee));

        assertThrows(
                IllegalStateException.class,
                () -> service.authenticate("john", "123")
        );
    }

    @Test
    void authenticate_wrongPassword() {
        UserEntity user = new UserEntity();
        user.setActive(true);
        user.setPassword("encoded".toCharArray());

        TraineeEntity trainee = new TraineeEntity();
        trainee.setUser(user);

        when(traineeRepository.findByUserUserName("john"))
                .thenReturn(Optional.of(trainee));
        when(bcrypt.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> service.authenticate("john", "wrong")
        );
    }

    // ------------------ getTraineeByUsername tests ------------------
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
