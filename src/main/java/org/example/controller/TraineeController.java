package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TraineeRegistrationRequest;
import org.example.dto.request.TraineeTrainingRequest;
import org.example.dto.request.TraineeUpdateRequest;
import org.example.dto.response.*;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createTrainee(@RequestBody TraineeRegistrationRequest traineeRequest) {
        System.out.println(traineeRequest.getFirstname());
        System.out.println(traineeRequest.getLastname());
        AuthResponse authResponse = traineeService.createTrainee(traineeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @Operation(
            summary = "Get trainee profile",
            description = "Retrieves the profile of a trainee by their username, including personal details, active status, assigned trainers, and profile information."
    )
    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getTrainee(
            @Parameter(description = "Username of the trainee", required = true, example = "trainee123")
            @PathVariable("username") String username) {
        TraineeProfileResponse traineeProfileResponse = traineeService.getTrainee(username);
        return ResponseEntity.status(HttpStatus.OK).body(traineeProfileResponse);
    }

    @Operation(
            summary = "Get available trainers for trainee",
            description = "Returns a list of active trainers who are not yet assigned to the specified trainee."
    )
    @GetMapping("/{username}/available-trainers")
    public ResponseEntity<List<TrainerResponse>> getActiveTrainersNotAssignedToTrainee(
            @Parameter(description = "Username of the trainee", required = true, example = "trainee123")
            @PathVariable("username") String username) {
        List<TrainerResponse> availableTrainers = traineeService.getActiveTrainersNotAssignedToTrainee(username);
        return ResponseEntity.status(HttpStatus.OK).body(availableTrainers);
    }

    @Operation(
            summary = "Update trainee details",
            description = "Updates an existing trainee's personal details, active status, address, and date of birth."
    )
    @PutMapping("/{username}")
    public TraineeUpdateResponse updateTrainee(@PathVariable("username") String username,@Valid @RequestBody TraineeUpdateRequest traineeUpdateRequest) {
        return traineeService.updateTrainee(username,traineeUpdateRequest);
    }

    @Operation(
            summary = "Delete a trainee",
            description = "Deletes the trainee with the specified username."
    )
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(
            @Parameter(description = "Username of the trainee", required = true, example = "trainee123")
            @PathVariable("username") String username) {
        traineeService.deleteTrainee(username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "Update trainee's assigned trainers",
            description = "Updates the list of trainers assigned to a trainee. Provide a list of trainer usernames to assign."

    )
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerResponse>> updateTraineeTrainersList(
            @Parameter(description = "Username of the trainee", required = true, example = "trainee123")
            @PathVariable("username") String username,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of trainer usernames",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(type = "string"))
                    )
            )
            List<String> trainerUsernames
    ) {
        List<TrainerResponse> trainers = traineeService.updateTraineesTrainerList(username, trainerUsernames);
        return ResponseEntity.status(HttpStatus.OK).body(trainers);
    }

    @Operation(
            summary = "Get trainee trainings by criteria",
            description = "Retrieves a list of trainings for a trainee based on optional filters like date range, training type, and trainer name."

    )
    @GetMapping("/{username}/training/search")
    public ResponseEntity<List<TrainingResponse>> getTraineeTrainingsByCriteria(
            @PathVariable("username") String username, TraineeTrainingRequest request) {
        List<TrainingResponse> list = traineeService.getTraineeTrainings(username,request);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
