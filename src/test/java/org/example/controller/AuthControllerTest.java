package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.request.AuthRequest;
import org.example.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        authRequest = new AuthRequest();
        authRequest.setUsername("john.doe");
        authRequest.setPassword("password123");
    }

    @Test
    void login_Success() throws Exception {
        String token = "token:jwt.token.here";
        when(authService.login(anyString(), anyString())).thenReturn(token);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(authService, times(1)).login("john.doe", "password123");
    }

    @Test
    void login_WithValidCredentials() throws Exception {
        String expectedToken = "\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9\"";
        when(authService.login(anyString(), anyString())).thenReturn(expectedToken);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));
    }

    @Test
    void login_WithDifferentUsername() throws Exception {
        authRequest.setUsername("jane.smith");
        String token = "different.token";
        when(authService.login(anyString(), anyString())).thenReturn(token);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());

        verify(authService, times(1)).login("jane.smith", "password123");
    }

    @Test
    void login_ServiceInvoked() throws Exception {
        when(authService.login(anyString(), anyString())).thenReturn("token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());

        verify(authService, times(1)).login(authRequest.getUsername(), authRequest.getPassword());
    }




    @Test
    void login_WithLongCredentials() throws Exception {
        authRequest.setUsername("very.long.username.with.many.characters");
        authRequest.setPassword("veryLongPasswordWithManyCharacters123!@#");
        when(authService.login(anyString(), anyString())).thenReturn("token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void login_VerifyRequestMapping() throws Exception {
        when(authService.login(anyString(), anyString())).thenReturn("token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void login_WithSpecialCharactersInPassword() throws Exception {
        authRequest.setPassword("P@ssw0rd!#$%");
        when(authService.login(anyString(), anyString())).thenReturn("token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());

        verify(authService, times(1)).login("john.doe", "P@ssw0rd!#$%");
    }
}