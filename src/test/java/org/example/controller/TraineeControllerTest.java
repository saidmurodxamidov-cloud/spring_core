package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.dto.request.TraineeRegistrationRequest;
import org.example.dto.request.TraineeTrainingRequest;
import org.example.dto.request.TraineeUpdateRequest;
import org.example.dto.response.*;
import org.example.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeController traineeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void createTrainee_WithoutAddress() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstname("John");
        request.setLastname("Doe");

        AuthResponse response = new AuthResponse("john.doe", "password123");
        when(traineeService.createTrainee(any(TraineeRegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }




    @Test
    void deleteTrainee_Success() throws Exception {
        doNothing().when(traineeService).deleteTrainee(anyString());

        mockMvc.perform(delete("/api/trainees/john.doe"))
                .andExpect(status().isOk());

        verify(traineeService, times(1)).deleteTrainee("john.doe");
    }

    @Test
    void deleteTrainee_DifferentUsername() throws Exception {
        doNothing().when(traineeService).deleteTrainee(anyString());

        mockMvc.perform(delete("/api/trainees/jane.smith"))
                .andExpect(status().isOk());

        verify(traineeService, times(1)).deleteTrainee("jane.smith");
    }


    @Test
    void getTraineeTrainingsByCriteria_NoFilters() throws Exception {
        when(traineeService.getTraineeTrainings(anyString(), any(TraineeTrainingRequest.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/trainees/john.doe/training/search"))
                .andExpect(status().isOk());

        verify(traineeService, times(1)).getTraineeTrainings(eq("john.doe"), any(TraineeTrainingRequest.class));
    }
}