package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.TrainingRequest;
import org.example.persistence.model.TrainingDTO;
import org.example.service.TrainingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<TrainingDTO> createTraining(
            @RequestBody TrainingRequest trainingRequest) {

        TrainingDTO created = trainingService.createTraining(trainingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/trainee/{username}")
    public ResponseEntity<List<TrainingDTO>> getAllTraineeTrainings(
            @PathVariable String username) {

        return ResponseEntity.ok(
                trainingService.getAllTraineeTrainings(username)
        );
    }

    @GetMapping("/trainer/{username}")
    public ResponseEntity<List<TrainingDTO>> getAllTrainerTrainings(
            @PathVariable String username) {

        return ResponseEntity.ok(
                trainingService.getAllTrainerTrainings(username)
        );
    }

    @GetMapping("/trainee/{username}/filter")
    public ResponseEntity<List<TrainingDTO>> filterTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingTypeName) {

        return ResponseEntity.ok(
                trainingService.getTraineeTrainings(
                        username,
                        fromDate,
                        toDate,
                        trainerName,
                        trainingTypeName
                )
        );
    }

    @GetMapping("/trainer/{username}/filter")
    public ResponseEntity<List<TrainingDTO>> filterTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false) String traineeName) {

        return ResponseEntity.ok(
                trainingService.getTrainerTrainings(
                        username,
                        fromDate,
                        toDate,
                        traineeName
                )
        );
    }

    // 6️⃣ Logged-in trainee trainings (recommended)
    @GetMapping("/me/trainee")
    public ResponseEntity<List<TrainingDTO>> myTraineeTrainings(
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                trainingService.getAllTraineeTrainings(user.getUsername())
        );
    }

    @GetMapping("/me/trainer")
    public ResponseEntity<List<TrainingDTO>> myTrainerTrainings(
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                trainingService.getAllTrainerTrainings(user.getUsername())
        );
    }
}
