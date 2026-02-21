package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TrainingAddRequest;
import org.example.persistence.model.TrainingDTO;
import org.example.service.TrainingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainings")
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<?> createTraining(@RequestBody @Valid TrainingAddRequest request){
        TrainingDTO dto = trainingService.addTraining(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    @DeleteMapping("/{training_id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long training_id){
        trainingService.deleteTraining(training_id);
        return ResponseEntity.ok().build();
    }
}
