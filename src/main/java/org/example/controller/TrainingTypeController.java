package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.persistence.model.TrainingTypeDTO;
import org.example.service.TrainingTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @PostMapping
    public ResponseEntity<TrainingTypeDTO> create(
            @RequestBody TrainingTypeDTO trainingTypeDTO) {

        TrainingTypeDTO created = trainingTypeService.create(trainingTypeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TrainingTypeDTO>> getAll() {
        return ResponseEntity.ok(
                trainingTypeService.getAllTrainingTypes()
        );
    }
}
