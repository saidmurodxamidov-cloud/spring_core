package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TrainerRegistrationRequest;
import org.example.dto.response.AuthResponse;
import org.example.persistence.model.TrainerDTO;
import org.example.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createTrainer(
            @Valid @RequestBody TrainerRegistrationRequest request) {

        AuthResponse authResponse = trainerService.createTrainer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerDTO> getTrainer(@PathVariable String username) {

        return ResponseEntity.ok(
                trainerService.getTrainerByUsername(username)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<TrainerDTO> me(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user) {

        return ResponseEntity.ok(
                trainerService.getTrainerByUsername(user.getUsername())
        );
    }

    @PutMapping("/me")
    public ResponseEntity<TrainerDTO> updateMe(
            @Valid @RequestBody TrainerDTO trainerDTO,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user) {

        trainerDTO.setUserName(user.getUsername());
        return ResponseEntity.ok(
                trainerService.updateTrainer(trainerDTO)
        );
    }

    @GetMapping("/unassigned/{traineeUsername}")
    public ResponseEntity<List<TrainerDTO>> getUnassignedTrainers(
            @PathVariable String traineeUsername) {

        return ResponseEntity.ok(
                trainerService.getTrainersNotAssignedToTrainee(traineeUsername)
        );
    }
}
