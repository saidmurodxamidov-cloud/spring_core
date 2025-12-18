package org.example.controller;

import org.example.dto.request.PasswordChangeRequest;
import org.example.dto.response.AuthResponse;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void changePassword_Success() {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("oldPassword123");
        request.setNewPassword("newPassword456");

        AuthResponse response = new AuthResponse("john.doe", "newPassword456");

        when(authentication.getName()).thenReturn("john.doe");
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(response);

        ResponseEntity<AuthResponse> result = userController.changePassword(request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("john.doe", result.getBody().getUsername());
        assertEquals("newPassword456", result.getBody().getPassword());
        verify(userService, times(1)).changePassword("john.doe", "oldPassword123", "newPassword456");
    }

    @Test
    void changePassword_WithDifferentPasswords() {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("currentPass");
        request.setNewPassword("newSecurePass123!");

        AuthResponse response = new AuthResponse("jane.smith", "newSecurePass123!");

        when(authentication.getName()).thenReturn("jane.smith");
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(response);

        ResponseEntity<AuthResponse> result = userController.changePassword(request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("jane.smith", result.getBody().getUsername());
        verify(userService, times(1)).changePassword("jane.smith", "currentPass", "newSecurePass123!");
    }

    @Test
    void changePassword_ServiceMethodInvoked() {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("old");
        request.setNewPassword("new");

        AuthResponse response = new AuthResponse("user", "new");

        when(authentication.getName()).thenReturn("user");
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(response);

        userController.changePassword(request, authentication);

        verify(authentication, times(1)).getName();
        verify(userService, times(1)).changePassword("user", "old", "new");
    }

    @Test
    void changePassword_VerifyUsername() {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("old");
        request.setNewPassword("new");

        AuthResponse response = new AuthResponse("testuser", "new");

        when(authentication.getName()).thenReturn("testuser");
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(response);

        ResponseEntity<AuthResponse> result = userController.changePassword(request, authentication);

        verify(userService, times(1)).changePassword(eq("testuser"), anyString(), anyString());
    }

    @Test
    void changePassword_ReturnsAuthResponse() {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("old");
        request.setNewPassword("new");

        AuthResponse response = new AuthResponse("user", "new");

        when(authentication.getName()).thenReturn("user");
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(response);

        ResponseEntity<AuthResponse> result = userController.changePassword(request, authentication);

        assertNotNull(result.getBody());
        assertTrue(result.getBody() instanceof AuthResponse);
    }

    @Test
    void toggleActiveStatus_ActivateUser() {
        when(userService.toggleUserActiveStatus(anyString())).thenReturn(true);

        ResponseEntity<?> result = userController.toggleActiveStatus("john.doe");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Status changed to true", result.getBody());
        verify(userService, times(1)).toggleUserActiveStatus("john.doe");
    }

    @Test
    void toggleActiveStatus_DeactivateUser() {
        when(userService.toggleUserActiveStatus(anyString())).thenReturn(false);

        ResponseEntity<?> result = userController.toggleActiveStatus("john.doe");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Status changed to false", result.getBody());
        verify(userService, times(1)).toggleUserActiveStatus("john.doe");
    }

    @Test
    void toggleActiveStatus_DifferentUsername() {
        when(userService.toggleUserActiveStatus(anyString())).thenReturn(true);

        ResponseEntity<?> result = userController.toggleActiveStatus("jane.smith");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService, times(1)).toggleUserActiveStatus("jane.smith");
    }

    @Test
    void toggleActiveStatus_ServiceMethodInvoked() {
        when(userService.toggleUserActiveStatus(anyString())).thenReturn(true);

        userController.toggleActiveStatus("user");

        verify(userService, times(1)).toggleUserActiveStatus("user");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void toggleActiveStatus_ReturnsOkStatus() {
        when(userService.toggleUserActiveStatus(anyString())).thenReturn(true);

        ResponseEntity<?> result = userController.toggleActiveStatus("john.doe");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void toggleActiveStatus_VerifyResponseMessage() {
        when(userService.toggleUserActiveStatus("john.doe")).thenReturn(true);

        ResponseEntity<?> result = userController.toggleActiveStatus("john.doe");

        assertTrue(result.getBody().toString().contains("Status changed to"));
        assertTrue(result.getBody().toString().contains("true"));
    }

    @Test
    void changePassword_WithEmptyOldPassword() {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("");
        request.setNewPassword("newPassword");

        AuthResponse response = new AuthResponse("user", "newPassword");

        when(authentication.getName()).thenReturn("user");
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(response);

        ResponseEntity<AuthResponse> result = userController.changePassword(request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService, times(1)).changePassword("user", "", "newPassword");
    }

    @Test
    void changePassword_WithEmptyNewPassword() {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("");

        AuthResponse response = new AuthResponse("user", "");

        when(authentication.getName()).thenReturn("user");
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(response);

        ResponseEntity<AuthResponse> result = userController.changePassword(request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService, times(1)).changePassword("user", "oldPassword", "");
    }

    @Test
    void changePassword_WithSpecialCharacters() {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("Old@Pass#123");
        request.setNewPassword("New!Pass$456");

        AuthResponse response = new AuthResponse("user", "New!Pass$456");

        when(authentication.getName()).thenReturn("user");
        when(userService.changePassword(anyString(), anyString(), anyString())).thenReturn(response);

        ResponseEntity<AuthResponse> result = userController.changePassword(request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService, times(1)).changePassword("user", "Old@Pass#123", "New!Pass$456");
    }

    @Test
    void toggleActiveStatus_MultipleUsers() {
        when(userService.toggleUserActiveStatus("user1")).thenReturn(true);
        when(userService.toggleUserActiveStatus("user2")).thenReturn(false);

        ResponseEntity<?> result1 = userController.toggleActiveStatus("user1");
        ResponseEntity<?> result2 = userController.toggleActiveStatus("user2");

        assertEquals("Status changed to true", result1.getBody());
        assertEquals("Status changed to false", result2.getBody());
        verify(userService, times(1)).toggleUserActiveStatus("user1");
        verify(userService, times(1)).toggleUserActiveStatus("user2");
    }
}