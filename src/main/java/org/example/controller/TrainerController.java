package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TrainerRequest;
import org.example.dto.request.TrainerTrainingsRequest;
import org.example.dto.request.TrainerUpdateRequest;
import org.example.dto.response.*;
import org.example.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
public class TrainerController {
    private final TrainerService trainerService;

    @Operation(
            summary = "Register a new trainer",
            description = "Creates a new trainer user with the specified first name, last name, and specializations. Returns the generated username and password."
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createTrainer(
            @Validated @RequestBody TrainerRequest trainerRequest) {
        AuthResponse authResponse = trainerService.createTrainer(trainerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @Operation(
            summary = "Get trainer profile",
            description = "Retrieves the profile of a trainer by their username, including personal details, active status, trainees, and specializations."
    )
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(
            @Parameter(description = "Username of the trainer to fetch", required = true, example = "trainer123")
            @PathVariable("username") String username) {
        TrainerProfileResponse trainer = trainerService.getTrainerProfile(username);
        return ResponseEntity.status(HttpStatus.OK).body(trainer);
    }

    @Operation(
            summary = "Update trainer information",
            description = "Updates an existing trainer's personal details, active status, and specializations. Requires trainer username in the request body."
    )
    @PutMapping("{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainer(
            @PathVariable("username") String username,
            @RequestBody @Valid TrainerUpdateRequest request) {
        TrainerUpdateResponse trainerUpdateResponse = trainerService.updateTrainer(username,request);
        return ResponseEntity.status(HttpStatus.OK).body(trainerUpdateResponse);
    }

    @Operation(
            summary = "Get trainer's trainings by criteria",
            description = "Retrieves a list of trainings for a specific trainer based on optional filters like date range and trainee name."
    )
    @PostMapping("/{username}/trainings/search")
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainingsByCriteria(
            @PathVariable("username") String username,
            @RequestBody TrainerTrainingsRequest request) {
        List<TrainingResponse> trainings = trainerService.getTrainerTrainings(username,request);
        return ResponseEntity.status(HttpStatus.OK).body(trainings);
    }
}
