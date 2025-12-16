package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TraineeRegistrationRequest;
import org.example.dto.response.AuthResponse;
import org.example.persistence.model.TraineeDTO;
import org.example.service.TraineeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trainees")
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createTrainee(
            @Valid @RequestBody TraineeRegistrationRequest traineeRegistrationRequest) {

        AuthResponse created = traineeService.createTrainee(traineeRegistrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<TraineeDTO> getTrainee(@RequestParam("username") String username){
        return ResponseEntity.status(HttpStatus.OK).body(traineeService.getTraineeByUsername(username));
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeDTO> updateTrainee(
            @PathVariable String username,
            @Valid @RequestBody TraineeDTO updateDTO) {

        TraineeDTO updated = traineeService.updateTrainee(username, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<Void> updateTrainers(
            @PathVariable String username,
            @RequestBody List<String> trainersUsernames) {

        traineeService.updateTrainersList(username, trainersUsernames);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }




}
