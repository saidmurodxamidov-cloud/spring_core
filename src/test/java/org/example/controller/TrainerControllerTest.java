package org.example.controller;

import org.example.dto.request.TrainerRequest;
import org.example.dto.request.TrainerTrainingsRequest;
import org.example.dto.request.TrainerUpdateRequest;
import org.example.dto.response.*;
import org.example.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createTrainer_Success() {
        TrainerRequest request = new TrainerRequest();
        request.setFirstname("Jane");
        request.setLastname("Smith");
        request.setSpecializations(Arrays.asList("Fitness", "Yoga"));

        AuthResponse authResponse = new AuthResponse("jane.smith", "password123");
        when(trainerService.createTrainer(any(TrainerRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = trainerController.createTrainer(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jane.smith", response.getBody().getUsername());
        assertEquals("password123", response.getBody().getPassword());
        verify(trainerService, times(1)).createTrainer(any(TrainerRequest.class));
    }

    @Test
    void createTrainer_WithSingleSpecialization() {
        TrainerRequest request = new TrainerRequest();
        request.setFirstname("Jane");
        request.setLastname("Smith");
        request.setSpecializations(Arrays.asList("Fitness"));

        AuthResponse authResponse = new AuthResponse("jane.smith", "password123");
        when(trainerService.createTrainer(any(TrainerRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = trainerController.createTrainer(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createTrainer_WithMultipleSpecializations() {
        TrainerRequest request = new TrainerRequest();
        request.setFirstname("Jane");
        request.setLastname("Smith");
        request.setSpecializations(Arrays.asList("Fitness", "Yoga", "Cardio"));

        AuthResponse authResponse = new AuthResponse("jane.smith", "password123");
        when(trainerService.createTrainer(any(TrainerRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = trainerController.createTrainer(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getTrainerProfile_Success() {
        TraineeResponse trainee = TraineeResponse.builder()
                .username("john.doe")
                .firstname("John")
                .lastname("Doe")
                .build();

        TrainerProfileResponse profileResponse = TrainerProfileResponse.builder()
                .firstname("Jane")
                .lastname("Smith")
                .isActive(true)
                .specialization(Collections.emptyList())
                .trainees(Arrays.asList(trainee))
                .build();

        when(trainerService.getTrainerProfile(anyString())).thenReturn(profileResponse);

        ResponseEntity<TrainerProfileResponse> response = trainerController.getTrainerProfile("jane.smith");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Jane", response.getBody().getFirstname());
        assertEquals("Smith", response.getBody().getLastname());
        assertTrue(response.getBody().isActive());
        assertEquals(1, response.getBody().getTrainees().size());
        verify(trainerService, times(1)).getTrainerProfile("jane.smith");
    }

    @Test
    void getTrainerProfile_WithSpecializations() {
        TrainingTypeResponse spec = new TrainingTypeResponse(1L, "Fitness");

        TrainerProfileResponse profileResponse = TrainerProfileResponse.builder()
                .firstname("Jane")
                .lastname("Smith")
                .isActive(true)
                .specialization(Arrays.asList(spec))
                .trainees(Collections.emptyList())
                .build();

        when(trainerService.getTrainerProfile(anyString())).thenReturn(profileResponse);

        ResponseEntity<TrainerProfileResponse> response = trainerController.getTrainerProfile("jane.smith");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getSpecialization().size());
        assertEquals("Fitness", response.getBody().getSpecialization().get(0).getTrainingTypeName());
    }

    @Test
    void getTrainerProfile_NoTrainees() {
        TrainerProfileResponse profileResponse = TrainerProfileResponse.builder()
                .firstname("Jane")
                .lastname("Smith")
                .isActive(true)
                .specialization(Collections.emptyList())
                .trainees(Collections.emptyList())
                .build();

        when(trainerService.getTrainerProfile(anyString())).thenReturn(profileResponse);

        ResponseEntity<TrainerProfileResponse> response = trainerController.getTrainerProfile("jane.smith");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getTrainees().isEmpty());
    }

    @Test
    void updateTrainer_Success() {
        TrainerUpdateRequest request = TrainerUpdateRequest.builder()
                .firstname("Jane")
                .lastname("Smith")
                .isActive(true)
                .build();

        TrainerUpdateResponse updateResponse = TrainerUpdateResponse.builder()
                .username("jane.smith")
                .firstname("Jane")
                .lastname("Smith")
                .isActive(true)
                .specialization(Collections.emptyList())
                .trainees(Collections.emptyList())
                .build();

        when(trainerService.updateTrainer(anyString(), any(TrainerUpdateRequest.class)))
                .thenReturn(updateResponse);

        ResponseEntity<TrainerUpdateResponse> response = trainerController.updateTrainer("jane.smith", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jane.smith", response.getBody().getUsername());
        assertEquals("Jane", response.getBody().getFirstname());
        assertTrue(response.getBody().isActive());
        verify(trainerService, times(1)).updateTrainer(eq("jane.smith"), any(TrainerUpdateRequest.class));
    }

    @Test
    void updateTrainer_ChangingActiveStatus() {
        TrainerUpdateRequest request = TrainerUpdateRequest.builder()
                .firstname("Jane")
                .lastname("Smith")
                .isActive(false)
                .build();

        TrainerUpdateResponse updateResponse = TrainerUpdateResponse.builder()
                .username("jane.smith")
                .firstname("Jane")
                .lastname("Smith")
                .isActive(false)
                .specialization(Collections.emptyList())
                .trainees(Collections.emptyList())
                .build();

        when(trainerService.updateTrainer(anyString(), any(TrainerUpdateRequest.class)))
                .thenReturn(updateResponse);

        ResponseEntity<TrainerUpdateResponse> response = trainerController.updateTrainer("jane.smith", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isActive());
    }

    @Test
    void updateTrainer_WithTrainees() {
        TrainerUpdateRequest request = TrainerUpdateRequest.builder()
                .firstname("Jane")
                .lastname("Smith")
                .isActive(true)
                .build();

        TraineeResponse trainee = TraineeResponse.builder()
                .username("john.doe")
                .firstname("John")
                .lastname("Doe")
                .build();

        TrainerUpdateResponse updateResponse = TrainerUpdateResponse.builder()
                .username("jane.smith")
                .firstname("Jane")
                .lastname("Smith")
                .isActive(true)
                .specialization(Collections.emptyList())
                .trainees(Arrays.asList(trainee))
                .build();

        when(trainerService.updateTrainer(anyString(), any(TrainerUpdateRequest.class)))
                .thenReturn(updateResponse);

        ResponseEntity<TrainerUpdateResponse> response = trainerController.updateTrainer("jane.smith", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTrainees().size());
        assertEquals("john.doe", response.getBody().getTrainees().get(0).getUsername());
    }

    @Test
    void getTrainerTrainingsByCriteria_Success() {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder()
                .from(LocalDate.of(2024, 1, 1))
                .to(LocalDate.of(2024, 12, 31))
                .traineeName("john.doe")
                .build();

        TrainingResponse training = TrainingResponse.builder()
                .trainingName("Morning Session")
                .trainingDate(LocalDate.now())
                .trainingType("Fitness")
                .durationInMinutes(60)
                .traineeUsername("john.doe")
                .build();

        when(trainerService.getTrainerTrainings(anyString(), any(TrainerTrainingsRequest.class)))
                .thenReturn(Arrays.asList(training));

        ResponseEntity<List<TrainingResponse>> response = trainerController.getTrainerTrainingsByCriteria("jane.smith", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Morning Session", response.getBody().get(0).getTrainingName());
        assertEquals(60, response.getBody().get(0).getDurationInMinutes());
        verify(trainerService, times(1)).getTrainerTrainings(eq("jane.smith"), any(TrainerTrainingsRequest.class));
    }

    @Test
    void getTrainerTrainingsByCriteria_WithDateRangeOnly() {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder()
                .from(LocalDate.of(2024, 1, 1))
                .to(LocalDate.of(2024, 12, 31))
                .build();

        when(trainerService.getTrainerTrainings(anyString(), any(TrainerTrainingsRequest.class)))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<TrainingResponse>> response = trainerController.getTrainerTrainingsByCriteria("jane.smith", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getTrainerTrainingsByCriteria_EmptyResult() {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder().build();

        when(trainerService.getTrainerTrainings(anyString(), any(TrainerTrainingsRequest.class)))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<TrainingResponse>> response = trainerController.getTrainerTrainingsByCriteria("jane.smith", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getTrainerTrainingsByCriteria_MultipleTrainings() {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder().build();

        TrainingResponse training1 = TrainingResponse.builder()
                .trainingName("Morning Session")
                .trainingDate(LocalDate.now())
                .trainingType("Fitness")
                .durationInMinutes(60)
                .build();

        TrainingResponse training2 = TrainingResponse.builder()
                .trainingName("Evening Session")
                .trainingDate(LocalDate.now())
                .trainingType("Yoga")
                .durationInMinutes(90)
                .build();

        when(trainerService.getTrainerTrainings(anyString(), any(TrainerTrainingsRequest.class)))
                .thenReturn(Arrays.asList(training1, training2));

        ResponseEntity<List<TrainingResponse>> response = trainerController.getTrainerTrainingsByCriteria("jane.smith", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getTrainerTrainingsByCriteria_WithTraineeName() {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder()
                .traineeName("john.doe")
                .build();

        when(trainerService.getTrainerTrainings(anyString(), any(TrainerTrainingsRequest.class)))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<TrainingResponse>> response = trainerController.getTrainerTrainingsByCriteria("jane.smith", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trainerService, times(1)).getTrainerTrainings(eq("jane.smith"), any(TrainerTrainingsRequest.class));
    }
}