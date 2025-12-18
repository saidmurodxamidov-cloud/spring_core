package org.example.service.impl;

import org.example.dto.response.AuthResponse;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceJpaTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcrypt;

    @InjectMocks
    private UserServiceJpa userService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .userName("john.doe")
                .passwordHash("encodedOldPassword")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .roles(new HashSet<>())
                .build();
    }

    @Test
    void changePassword_Success() {
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword456";
        String encodedNewPassword = "encodedNewPassword";

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
        when(bcrypt.matches(oldPassword, user.getPasswordHash())).thenReturn(true);
        when(bcrypt.encode(newPassword)).thenReturn(encodedNewPassword);

        AuthResponse response = userService.changePassword("john.doe", oldPassword, newPassword);

        assertNotNull(response);
        assertEquals("john.doe", response.getUsername());
        assertEquals(newPassword, response.getPassword());
        assertEquals(encodedNewPassword, user.getPasswordHash());
        verify(userRepository, times(1)).findByUserName("john.doe");
        verify(bcrypt, times(1)).matches(oldPassword, "encodedOldPassword");
        verify(bcrypt, times(1)).encode(newPassword);
    }

    @Test
    void changePassword_UserNotFound() {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.changePassword("unknown", "oldPass", "newPass"));
        verify(userRepository, times(1)).findByUserName("unknown");
        verify(bcrypt, never()).matches(anyString(), anyString());
        verify(bcrypt, never()).encode(anyString());
    }

    @Test
    void changePassword_IncorrectOldPassword() {
        String oldPassword = "wrongPassword";
        String newPassword = "newPassword456";

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
        when(bcrypt.matches(oldPassword, user.getPasswordHash())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> userService.changePassword("john.doe", oldPassword, newPassword));
        verify(bcrypt, times(1)).matches(oldPassword, user.getPasswordHash());
        verify(bcrypt, never()).encode(anyString());
    }

    @Test
    void changePassword_SameAsOldPassword() {
        String password = "samePassword123";
        String encodedPassword = "encodedSamePassword";

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
        when(bcrypt.matches(password, user.getPasswordHash())).thenReturn(true);
        when(bcrypt.encode(password)).thenReturn(encodedPassword);

        AuthResponse response = userService.changePassword("john.doe", password, password);

        assertNotNull(response);
        assertEquals(password, response.getPassword());
        assertEquals(encodedPassword, user.getPasswordHash());
    }

    @Test
    void changePassword_EmptyNewPassword() {
        String oldPassword = "oldPassword123";
        String newPassword = "";
        String encodedNewPassword = "encodedEmpty";

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
        when(bcrypt.matches(oldPassword, user.getPasswordHash())).thenReturn(true);
        when(bcrypt.encode(newPassword)).thenReturn(encodedNewPassword);

        AuthResponse response = userService.changePassword("john.doe", oldPassword, newPassword);

        assertNotNull(response);
        assertEquals("", response.getPassword());
    }

    @Test
    void changePassword_VerifyPasswordUpdate() {
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword456";
        String encodedNewPassword = "encodedNewPassword";

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
        when(bcrypt.matches(oldPassword, user.getPasswordHash())).thenReturn(true);
        when(bcrypt.encode(newPassword)).thenReturn(encodedNewPassword);

        userService.changePassword("john.doe", oldPassword, newPassword);

        assertEquals(encodedNewPassword, user.getPasswordHash());
    }

    @Test
    void toggleUserActiveStatus_ActivateUser() {
        user.setActive(false);
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

        Boolean result = userService.toggleUserActiveStatus("john.doe");

        assertTrue(result);
        assertTrue(user.isActive());
        verify(userRepository, times(1)).findByUserName("john.doe");
    }

    @Test
    void toggleUserActiveStatus_DeactivateUser() {
        user.setActive(true);
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

        Boolean result = userService.toggleUserActiveStatus("john.doe");

        assertFalse(result);
        assertFalse(user.isActive());
        verify(userRepository, times(1)).findByUserName("john.doe");
    }

    @Test
    void toggleUserActiveStatus_UserNotFound() {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.toggleUserActiveStatus("unknown"));
        verify(userRepository, times(1)).findByUserName("unknown");
    }

    @Test
    void toggleUserActiveStatus_MultipleToggles() {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

        Boolean firstToggle = userService.toggleUserActiveStatus("john.doe");
        assertFalse(firstToggle);
        assertFalse(user.isActive());

        Boolean secondToggle = userService.toggleUserActiveStatus("john.doe");
        assertTrue(secondToggle);
        assertTrue(user.isActive());

        verify(userRepository, times(2)).findByUserName("john.doe");
    }

    @Test
    void toggleUserActiveStatus_VerifyStatusChange() {
        user.setActive(true);
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

        userService.toggleUserActiveStatus("john.doe");

        assertFalse(user.isActive());
    }

    @Test
    void changePassword_NullOldPassword() {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
        when(bcrypt.matches(null, user.getPasswordHash())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> userService.changePassword("john.doe", null, "newPass"));
    }

    @Test
    void changePassword_NullNewPassword() {
        String oldPassword = "oldPassword123";

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
        when(bcrypt.matches(oldPassword, user.getPasswordHash())).thenReturn(true);
        when(bcrypt.encode(null)).thenReturn("encodedNull");

        AuthResponse response = userService.changePassword("john.doe", oldPassword, null);

        assertNotNull(response);
        assertNull(response.getPassword());
    }

    @Test
    void changePassword_LongPassword() {
        String oldPassword = "oldPassword123";
        String newPassword = "thisIsAVeryLongPasswordWithManyCharacters12345!@#$%";
        String encodedNewPassword = "encodedLongPassword";

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
        when(bcrypt.matches(oldPassword, user.getPasswordHash())).thenReturn(true);
        when(bcrypt.encode(newPassword)).thenReturn(encodedNewPassword);

        AuthResponse response = userService.changePassword("john.doe", oldPassword, newPassword);

        assertNotNull(response);
        assertEquals(newPassword, response.getPassword());
    }

    @Test
    void toggleUserActiveStatus_InitiallyActive() {
        assertTrue(user.isActive());
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

        Boolean result = userService.toggleUserActiveStatus("john.doe");

        assertFalse(result);
        assertFalse(user.isActive());
    }

    @Test
    void toggleUserActiveStatus_InitiallyInactive() {
        user.setActive(false);
        assertFalse(user.isActive());
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

        Boolean result = userService.toggleUserActiveStatus("john.doe");

        assertTrue(result);
        assertTrue(user.isActive());
    }

    @Test
    void changePassword_ReturnedPasswordIsPlainText() {
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword456";
        String encodedNewPassword = "encodedNewPassword";

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
        when(bcrypt.matches(oldPassword, user.getPasswordHash())).thenReturn(true);
        when(bcrypt.encode(newPassword)).thenReturn(encodedNewPassword);

        AuthResponse response = userService.changePassword("john.doe", oldPassword, newPassword);

        assertEquals(newPassword, response.getPassword());
        assertNotEquals(encodedNewPassword, response.getPassword());
    }
}